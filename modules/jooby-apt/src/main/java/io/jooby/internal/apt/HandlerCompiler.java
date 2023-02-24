/*
 * Jooby https://jooby.io
 * Apache License Version 2.0 https://jooby.io/LICENSE.txt
 * Copyright 2014 Edgar Espina
 */
package io.jooby.internal.apt;

import static io.jooby.internal.apt.JoobyTypes.Provider;
import static io.jooby.internal.apt.JoobyTypes.StatusCode;
import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ACC_SYNTHETIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.POP;
import static org.objectweb.asm.Type.getMethodDescriptor;
import static org.objectweb.asm.Type.getType;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import io.jooby.internal.apt.asm.NameGenerator;
import io.jooby.internal.apt.asm.ParamWriter;

public class HandlerCompiler {

  private static final Type OBJ = getType(Object.class);
  private static final String PROVIDER_DESCRIPTOR = getMethodDescriptor(OBJ);

  private TypeDefinition owner;
  private ExecutableElement executable;
  private ProcessingEnvironment environment;
  private String httpMethod;
  private String pattern;
  private Types typeUtils;
  private TypeMirror annotation;

  public HandlerCompiler(
      ProcessingEnvironment environment,
      TypeElement owner,
      ExecutableElement executable,
      TypeElement httpMethod,
      String pattern) {
    this.httpMethod = httpMethod.getSimpleName().toString().toLowerCase();
    this.annotation = httpMethod.asType();
    this.pattern = leadingSlash(pattern);
    this.environment = environment;
    this.executable = executable;
    this.typeUtils = environment.getTypeUtils();
    this.owner = new TypeDefinition(typeUtils, owner.asType());
  }

  public ExecutableElement getExecutable() {
    return executable;
  }

  public String getPattern() {
    return pattern;
  }

  public String getHttpMethod() {
    return httpMethod;
  }

  public TypeDefinition getReturnType() {
    return new TypeDefinition(typeUtils, executable.getReturnType());
  }

  public List<String> getConsumes() {
    return mediaType(executable, annotation, "consumes", Annotations.CONSUMES_PARAMS);
  }

  public List<String> getProduces() {
    return mediaType(executable, annotation, "produces", Annotations.PRODUCES_PARAMS);
  }

  public void compile(
      String internalName,
      ClassWriter writer,
      MethodVisitor methodVisitor,
      NameGenerator nameRegistry)
      throws Exception {
    String key =
        httpMethod + camelCase(executable.getSimpleName().toString()) + arguments(executable);
    String methodName = nameRegistry.generate(key);

    methodVisitor.visitInvokeDynamicInsn(
        "apply",
        "(Ljakarta/inject/Provider;)Lio/jooby/Route$Handler;",
        new Handle(
            Opcodes.H_INVOKESTATIC,
            "java/lang/invoke/LambdaMetafactory",
            "metafactory",
            "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;",
            false),
        new Object[] {
          Type.getType("(Lio/jooby/Context;)Ljava/lang/Object;"),
          new Handle(
              Opcodes.H_INVOKESTATIC,
              internalName,
              methodName,
              "(Ljakarta/inject/Provider;Lio/jooby/Context;)Ljava/lang/Object;",
              false),
          Type.getType("(Lio/jooby/Context;)Ljava/lang/Object;")
        });

    /** Apply implementation: */
    apply(writer, internalName, methodName, nameRegistry);
  }

  private String camelCase(String name) {
    if (name.length() > 1) {
      return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
    return name.toUpperCase();
  }

  private String arguments(ExecutableElement executable) {
    StringBuilder buff = new StringBuilder();
    for (VariableElement var : executable.getParameters()) {
      buff.append("$").append(var.getSimpleName());
    }
    return buff.toString();
  }

  private void apply(
      ClassWriter writer, String moduleInternalName, String lambdaName, NameGenerator registry)
      throws Exception {
    Type owner = getController().toJvmType();
    String methodName = executable.getSimpleName().toString();
    String methodDescriptor = methodDescriptor();
    MethodVisitor apply =
        writer.visitMethod(
            ACC_PRIVATE | ACC_STATIC | ACC_SYNTHETIC,
            lambdaName,
            "(Ljakarta/inject/Provider;Lio/jooby/Context;)Ljava/lang/Object;",
            null,
            new String[] {"java/lang/Exception"});
    apply.visitParameter("provider", ACC_FINAL | ACC_SYNTHETIC);
    apply.visitParameter("ctx", ACC_SYNTHETIC);

    apply.visitCode();

    Label sourceStart = new Label();
    apply.visitLabel(sourceStart);

    /** provider.get() */
    apply.visitVarInsn(ALOAD, 0);
    apply.visitMethodInsn(
        INVOKEINTERFACE, Provider.getInternalName(), "get", PROVIDER_DESCRIPTOR, true);
    apply.visitTypeInsn(CHECKCAST, owner.getInternalName());
    apply.visitVarInsn(ASTORE, 2);
    apply.visitVarInsn(ALOAD, 2);

    /** Arguments. */
    processArguments(writer, apply, owner, moduleInternalName, registry);

    setDefaultResponseType(apply);

    /** Invoke. */
    apply.visitMethodInsn(
        INVOKEVIRTUAL, owner.getInternalName(), methodName, methodDescriptor, false);

    processReturnType(apply);

    apply.visitEnd();
  }

  public boolean isSuspendFunction() {
    List<? extends VariableElement> parameters = executable.getParameters();
    if (parameters.isEmpty()) {
      return false;
    }
    VariableElement last = parameters.get(parameters.size() - 1);
    return isSuspendFunction(last);
  }

  private boolean isSuspendFunction(VariableElement parameter) {
    String type = ParamDefinition.create(environment, parameter).getType().getRawType().toString();
    return type.equals("kotlin.coroutines.Continuation");
  }

  private void processArguments(
      ClassWriter classWriter,
      MethodVisitor visitor,
      Type controller,
      String moduleInternalName,
      NameGenerator registry)
      throws Exception {
    for (VariableElement var : executable.getParameters()) {
      if (isSuspendFunction(var)) {
        visitor.visitVarInsn(ALOAD, 1);
        visitor.visitMethodInsn(
            INVOKEINTERFACE, "io/jooby/Context", "getAttributes", "()Ljava/util/Map;", true);
        visitor.visitLdcInsn("___continuation");
        visitor.visitMethodInsn(
            INVOKEINTERFACE,
            "java/util/Map",
            "remove",
            "(Ljava/lang/Object;)Ljava/lang/Object;",
            true);
        visitor.visitTypeInsn(CHECKCAST, "kotlin/coroutines/Continuation");
      } else {
        visitor.visitVarInsn(ALOAD, 1);
        ParamDefinition param = ParamDefinition.create(environment, var);
        ParamWriter writer = param.newWriter();
        writer.accept(classWriter, controller, moduleInternalName, visitor, param, registry);
      }
    }
  }

  private void setDefaultResponseType(MethodVisitor visitor) throws Exception {
    TypeKind kind = executable.getReturnType().getKind();
    if (kind == TypeKind.VOID && getHttpMethod().equalsIgnoreCase("DELETE")) {
      visitor.visitVarInsn(ALOAD, 1);
      visitor.visitFieldInsn(
          GETSTATIC, StatusCode.getInternalName(), "NO_CONTENT", StatusCode.getDescriptor());
      visitor.visitMethodInsn(
          INVOKEINTERFACE,
          MethodDescriptor.Context.setResponseCode().getDeclaringType().getInternalName(),
          MethodDescriptor.Context.setResponseCode().getName(),
          MethodDescriptor.Context.setResponseCode().getDescriptor(),
          true);
      visitor.visitInsn(POP);
    }
  }

  private void processReturnType(MethodVisitor visitor) throws Exception {
    TypeKind kind = executable.getReturnType().getKind();
    if (kind == TypeKind.VOID) {
      visitor.visitVarInsn(ALOAD, 1);
      visitor.visitMethodInsn(
          INVOKEINTERFACE,
          MethodDescriptor.Context.getResponseCode().getDeclaringType().getInternalName(),
          MethodDescriptor.Context.getResponseCode().getName(),
          MethodDescriptor.Context.getResponseCode().getDescriptor(),
          true);
    } else {
      Method wrapper = Primitives.wrapper(kind);
      if (wrapper == null) {
        TypeDefinition returnType = getReturnType();
        if (returnType.is(StatusCode.getClassName())) {
          visitor.visitVarInsn(ASTORE, 2);
          visitor.visitVarInsn(ALOAD, 1);
          visitor.visitVarInsn(ALOAD, 2);
          visitor.visitMethodInsn(
              INVOKEINTERFACE,
              MethodDescriptor.Context.setResponseCode().getDeclaringType().getInternalName(),
              MethodDescriptor.Context.setResponseCode().getName(),
              MethodDescriptor.Context.setResponseCode().getDescriptor(),
              true);
          visitor.visitInsn(POP);
          visitor.visitVarInsn(ALOAD, 2);
        }
      } else {
        // Primitive wrapper
        visitor.visitMethodInsn(
            INVOKESTATIC,
            Type.getInternalName(wrapper.getDeclaringClass()),
            wrapper.getName(),
            getMethodDescriptor(wrapper),
            false);
      }
    }
    visitor.visitInsn(ARETURN);

    visitor.visitMaxs(0, 0);
  }

  public TypeDefinition getController() {
    return owner;
  }

  private String methodDescriptor() {
    Types typeUtils = environment.getTypeUtils();
    Type returnType = new TypeDefinition(typeUtils, executable.getReturnType()).toJvmType();
    Type[] arguments =
        executable.getParameters().stream()
            .map(var -> new TypeDefinition(typeUtils, var.asType()).toJvmType())
            .toArray(Type[]::new);
    return Type.getMethodDescriptor(returnType, arguments);
  }

  private List<String> mediaType(
      ExecutableElement element, TypeMirror annotation, String property, Set<String> types) {
    List<String> result =
        element.getAnnotationMirrors().stream()
            .filter(it -> it.getAnnotationType().equals(annotation))
            .findFirst()
            .map(it -> Annotations.attribute(it, property))
            .orElse(Collections.emptyList());
    if (result.size() == 0) {
      return mediaType(element, types);
    }
    return result;
  }

  private List<String> mediaType(Element element, Set<String> types) {
    return element.getAnnotationMirrors().stream()
        .filter(it -> types.contains(it.getAnnotationType().toString()))
        .findFirst()
        .map(it -> Annotations.attribute(it, "value"))
        .orElseGet(
            () -> {
              if (element instanceof ExecutableElement) {
                return mediaType(element.getEnclosingElement(), types);
              }
              return Collections.emptyList();
            });
  }

  /**
   * Ensure path start with a <code>/</code>(leading slash).
   *
   * @param path Path to process.
   * @return Path with leading slash.
   */
  static String leadingSlash(String path) {
    if (path == null || path.length() == 0 || path.equals("/")) {
      return "/";
    }
    return path.charAt(0) == '/' ? path : "/" + path;
  }
}
