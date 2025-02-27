/*
 * Jooby https://jooby.io
 * Apache License Version 2.0 https://jooby.io/LICENSE.txt
 * Copyright 2014 Edgar Espina
 */
package io.jooby.openapi;

import examples.*;
import kt.KtMinApp;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OpenAPIYamlTest {

  @OpenAPITest(value = MinApp.class)
  public void shouldGenerateMinApp(OpenAPIResult result) {
    assertEquals(
        "openapi: 3.0.1\n"
            + "info:\n"
            + "  title: Base Yaml API\n"
            + "  description: Min API description\n"
            + "  version: \"1.0\"\n"
            + "servers:\n"
            + "- url: /myapp/path\n"
            + "paths:\n"
            + "  /api/pets:\n"
            + "    get:\n"
            + "      operationId: getApiPets\n"
            + "      parameters:\n"
            + "      - name: id\n"
            + "        in: query\n"
            + "        schema:\n"
            + "          type: integer\n"
            + "          format: int64\n"
            + "      - name: name\n"
            + "        in: query\n"
            + "        schema:\n"
            + "          type: string\n"
            + "      - name: start\n"
            + "        in: query\n"
            + "        schema:\n"
            + "          type: integer\n"
            + "          format: int32\n"
            + "      - name: max\n"
            + "        in: query\n"
            + "        schema:\n"
            + "          type: integer\n"
            + "          format: int32\n"
            + "      responses:\n"
            + "        \"200\":\n"
            + "          description: Success\n"
            + "          content:\n"
            + "            application/json:\n"
            + "              schema:\n"
            + "                type: array\n"
            + "                items:\n"
            + "                  $ref: \"#/components/schemas/Pet\"\n"
            + "    put:\n"
            + "      operationId: updatePet\n"
            + "      requestBody:\n"
            + "        content:\n"
            + "          application/json:\n"
            + "            schema:\n"
            + "              $ref: \"#/components/schemas/Pet\"\n"
            + "      responses:\n"
            + "        \"200\":\n"
            + "          description: Success\n"
            + "          content:\n"
            + "            application/json:\n"
            + "              schema:\n"
            + "                $ref: \"#/components/schemas/Pet\"\n"
            + "    post:\n"
            + "      operationId: createPet\n"
            + "      requestBody:\n"
            + "        description: Pet to create\n"
            + "        content:\n"
            + "          application/json:\n"
            + "            schema:\n"
            + "              $ref: \"#/components/schemas/Pet\"\n"
            + "      responses:\n"
            + "        \"200\":\n"
            + "          description: Success\n"
            + "          content:\n"
            + "            application/json:\n"
            + "              schema:\n"
            + "                $ref: \"#/components/schemas/Pet\"\n"
            + "    patch:\n"
            + "      operationId: updatePet2\n"
            + "      requestBody:\n"
            + "        content:\n"
            + "          application/json:\n"
            + "            schema:\n"
            + "              $ref: \"#/components/schemas/Pet\"\n"
            + "      responses:\n"
            + "        \"200\":\n"
            + "          description: Success\n"
            + "          content:\n"
            + "            application/json:\n"
            + "              schema:\n"
            + "                $ref: \"#/components/schemas/Pet\"\n"
            + "  /api/pets/{id}:\n"
            + "    get:\n"
            + "      tags:\n"
            + "      - find\n"
            + "      - query\n"
            + "      summary: Find a pet by ID\n"
            + "      description: Find a pet by ID or throws a 404\n"
            + "      operationId: findPetById\n"
            + "      parameters:\n"
            + "      - name: id\n"
            + "        in: path\n"
            + "        description: Pet ID\n"
            + "        required: true\n"
            + "        schema:\n"
            + "          type: integer\n"
            + "          format: int64\n"
            + "      responses:\n"
            + "        \"200\":\n"
            + "          description: Success\n"
            + "          content:\n"
            + "            application/json:\n"
            + "              schema:\n"
            + "                $ref: \"#/components/schemas/Pet\"\n"
            + "    delete:\n"
            + "      operationId: deletePet\n"
            + "      parameters:\n"
            + "      - name: id\n"
            + "        in: path\n"
            + "        required: true\n"
            + "        schema:\n"
            + "          type: integer\n"
            + "          format: int64\n"
            + "      responses:\n"
            + "        \"204\":\n"
            + "          description: No Content\n"
            + "  /api/pets/form:\n"
            + "    post:\n"
            + "      operationId: formPet\n"
            + "      requestBody:\n"
            + "        content:\n"
            + "          application/x-www-form-urlencoded:\n"
            + "            schema:\n"
            + "              $ref: \"#/components/schemas/Pet\"\n"
            + "      responses:\n"
            + "        \"200\":\n"
            + "          description: Success\n"
            + "          content:\n"
            + "            application/json:\n"
            + "              schema:\n"
            + "                $ref: \"#/components/schemas/Pet\"\n"
            + "components:\n"
            + "  schemas:\n"
            + "    Pet:\n"
            + "      type: object\n"
            + "      properties:\n"
            + "        id:\n"
            + "          type: integer\n"
            + "          format: int64\n"
            + "        name:\n"
            + "          type: string\n"
            + "    PetQuery:\n"
            + "      type: object\n"
            + "      properties:\n"
            + "        id:\n"
            + "          type: integer\n"
            + "          format: int64\n"
            + "        name:\n"
            + "          type: string\n"
            + "        start:\n"
            + "          type: integer\n"
            + "          format: int32\n"
            + "        max:\n"
            + "          type: integer\n"
            + "          format: int32\n",
        result.toYaml());
  }

  @OpenAPITest(KtMinApp.class)
  public void shouldGenerateKtMinApp(OpenAPIResult result) {
    assertEquals(
        "openapi: 3.0.1\n"
            + "info:\n"
            + "  title: Min API\n"
            + "  description: Min API description\n"
            + "  version: \"1.0\"\n"
            + "paths:\n"
            + "  /api/pets:\n"
            + "    get:\n"
            + "      operationId: getApiPets\n"
            + "      parameters:\n"
            + "      - name: id\n"
            + "        in: query\n"
            + "        schema:\n"
            + "          type: integer\n"
            + "          format: int64\n"
            + "      - name: name\n"
            + "        in: query\n"
            + "        schema:\n"
            + "          type: string\n"
            + "      - name: start\n"
            + "        in: query\n"
            + "        schema:\n"
            + "          type: integer\n"
            + "          format: int32\n"
            + "      - name: max\n"
            + "        in: query\n"
            + "        schema:\n"
            + "          type: integer\n"
            + "          format: int32\n"
            + "      responses:\n"
            + "        \"200\":\n"
            + "          description: Success\n"
            + "          content:\n"
            + "            application/json:\n"
            + "              schema:\n"
            + "                type: array\n"
            + "                items:\n"
            + "                  $ref: \"#/components/schemas/Pet\"\n"
            + "    put:\n"
            + "      operationId: updatePet\n"
            + "      requestBody:\n"
            + "        content:\n"
            + "          application/json:\n"
            + "            schema:\n"
            + "              $ref: \"#/components/schemas/Pet\"\n"
            + "      responses:\n"
            + "        \"200\":\n"
            + "          description: Success\n"
            + "          content:\n"
            + "            application/json:\n"
            + "              schema:\n"
            + "                $ref: \"#/components/schemas/Pet\"\n"
            + "    post:\n"
            + "      operationId: createPet\n"
            + "      requestBody:\n"
            + "        description: Pet to create\n"
            + "        content:\n"
            + "          application/json:\n"
            + "            schema:\n"
            + "              $ref: \"#/components/schemas/Pet\"\n"
            + "      responses:\n"
            + "        \"200\":\n"
            + "          description: Success\n"
            + "          content:\n"
            + "            application/json:\n"
            + "              schema:\n"
            + "                $ref: \"#/components/schemas/Pet\"\n"
            + "    patch:\n"
            + "      operationId: updatePet2\n"
            + "      requestBody:\n"
            + "        content:\n"
            + "          application/json:\n"
            + "            schema:\n"
            + "              $ref: \"#/components/schemas/Pet\"\n"
            + "      responses:\n"
            + "        \"200\":\n"
            + "          description: Success\n"
            + "          content:\n"
            + "            application/json:\n"
            + "              schema:\n"
            + "                $ref: \"#/components/schemas/Pet\"\n"
            + "  /api/pets/{id}:\n"
            + "    get:\n"
            + "      tags:\n"
            + "      - find\n"
            + "      - query\n"
            + "      summary: Find a pet by ID\n"
            + "      description: Find a pet by ID or throws a 404\n"
            + "      operationId: findPetById\n"
            + "      parameters:\n"
            + "      - name: id\n"
            + "        in: path\n"
            + "        description: Pet ID\n"
            + "        required: true\n"
            + "        schema:\n"
            + "          type: integer\n"
            + "          format: int64\n"
            + "      responses:\n"
            + "        \"200\":\n"
            + "          description: Success\n"
            + "          content:\n"
            + "            application/json:\n"
            + "              schema:\n"
            + "                $ref: \"#/components/schemas/Pet\"\n"
            + "    delete:\n"
            + "      operationId: deletePet\n"
            + "      parameters:\n"
            + "      - name: id\n"
            + "        in: path\n"
            + "        required: true\n"
            + "        schema:\n"
            + "          type: integer\n"
            + "          format: int64\n"
            + "      responses:\n"
            + "        \"204\":\n"
            + "          description: No Content\n"
            + "  /api/pets/form:\n"
            + "    post:\n"
            + "      operationId: formPet\n"
            + "      requestBody:\n"
            + "        content:\n"
            + "          application/x-www-form-urlencoded:\n"
            + "            schema:\n"
            + "              $ref: \"#/components/schemas/Pet\"\n"
            + "      responses:\n"
            + "        \"200\":\n"
            + "          description: Success\n"
            + "          content:\n"
            + "            application/json:\n"
            + "              schema:\n"
            + "                $ref: \"#/components/schemas/Pet\"\n"
            + "components:\n"
            + "  schemas:\n"
            + "    Pet:\n"
            + "      type: object\n"
            + "      properties:\n"
            + "        id:\n"
            + "          type: integer\n"
            + "          format: int64\n"
            + "        name:\n"
            + "          type: string\n"
            + "    PetQuery:\n"
            + "      type: object\n"
            + "      properties:\n"
            + "        id:\n"
            + "          type: integer\n"
            + "          format: int64\n"
            + "        name:\n"
            + "          type: string\n"
            + "        start:\n"
            + "          type: integer\n"
            + "          format: int32\n"
            + "        max:\n"
            + "          type: integer\n"
            + "          format: int32\n",
        result.toYaml());
  }

  @OpenAPITest(FormApp.class)
  public void shouldDoForm(OpenAPIResult result) {
    assertEquals(
        "openapi: 3.0.1\n"
            + "info:\n"
            + "  title: Form API\n"
            + "  description: Form API description\n"
            + "  version: \"1.0\"\n"
            + "paths:\n"
            + "  /single:\n"
            + "    post:\n"
            + "      operationId: postSingle\n"
            + "      requestBody:\n"
            + "        content:\n"
            + "          application/x-www-form-urlencoded:\n"
            + "            schema:\n"
            + "              required:\n"
            + "              - name\n"
            + "              type: object\n"
            + "              properties:\n"
            + "                name:\n"
            + "                  type: string\n"
            + "      responses:\n"
            + "        \"200\":\n"
            + "          description: Success\n"
            + "          content:\n"
            + "            application/json:\n"
            + "              schema:\n"
            + "                type: string\n"
            + "  /multiple:\n"
            + "    post:\n"
            + "      operationId: postMultiple\n"
            + "      requestBody:\n"
            + "        content:\n"
            + "          multipart/form-data:\n"
            + "            schema:\n"
            + "              required:\n"
            + "              - firstname\n"
            + "              - lastname\n"
            + "              - picture\n"
            + "              type: object\n"
            + "              properties:\n"
            + "                firstname:\n"
            + "                  type: string\n"
            + "                lastname:\n"
            + "                  type: string\n"
            + "                picture:\n"
            + "                  type: string\n"
            + "                  format: binary\n"
            + "      responses:\n"
            + "        \"200\":\n"
            + "          description: Success\n"
            + "          content:\n"
            + "            application/json:\n"
            + "              schema:\n"
            + "                type: string\n"
            + "  /bean:\n"
            + "    post:\n"
            + "      operationId: postBean\n"
            + "      requestBody:\n"
            + "        content:\n"
            + "          application/x-www-form-urlencoded:\n"
            + "            schema:\n"
            + "              $ref: \"#/components/schemas/AForm\"\n"
            + "      responses:\n"
            + "        \"200\":\n"
            + "          description: Success\n"
            + "          content:\n"
            + "            application/json:\n"
            + "              schema:\n"
            + "                type: string\n"
            + "components:\n"
            + "  schemas:\n"
            + "    AForm:\n"
            + "      type: object\n"
            + "      properties:\n"
            + "        name:\n"
            + "          type: string\n"
            + "        picture:\n"
            + "          type: string\n"
            + "          format: binary\n",
        result.toYaml());
  }

  @OpenAPITest(FormMvcApp.class)
  public void shouldDoMvcForm(OpenAPIResult result) {
    assertEquals(
        "openapi: 3.0.1\n"
            + "info:\n"
            + "  title: FormMvc API\n"
            + "  description: FormMvc API description\n"
            + "  version: \"1.0\"\n"
            + "paths:\n"
            + "  /single:\n"
            + "    post:\n"
            + "      operationId: postSingle\n"
            + "      requestBody:\n"
            + "        content:\n"
            + "          multipart/form-data:\n"
            + "            schema:\n"
            + "              type: object\n"
            + "              properties:\n"
            + "                name:\n"
            + "                  type: string\n"
            + "      responses:\n"
            + "        \"200\":\n"
            + "          description: Success\n"
            + "          content:\n"
            + "            application/json:\n"
            + "              schema:\n"
            + "                type: string\n"
            + "  /multiple:\n"
            + "    post:\n"
            + "      operationId: postMultiple\n"
            + "      requestBody:\n"
            + "        content:\n"
            + "          multipart/form-data:\n"
            + "            schema:\n"
            + "              type: object\n"
            + "              properties:\n"
            + "                firstname:\n"
            + "                  type: string\n"
            + "                lastname:\n"
            + "                  type: string\n"
            + "                picture:\n"
            + "                  type: string\n"
            + "                  format: binary\n"
            + "      responses:\n"
            + "        \"200\":\n"
            + "          description: Success\n"
            + "          content:\n"
            + "            application/json:\n"
            + "              schema:\n"
            + "                type: string\n"
            + "  /bean:\n"
            + "    post:\n"
            + "      operationId: postBean\n"
            + "      requestBody:\n"
            + "        content:\n"
            + "          multipart/form-data:\n"
            + "            schema:\n"
            + "              $ref: \"#/components/schemas/AForm\"\n"
            + "      responses:\n"
            + "        \"200\":\n"
            + "          description: Success\n"
            + "          content:\n"
            + "            application/json:\n"
            + "              schema:\n"
            + "                type: string\n"
            + "components:\n"
            + "  schemas:\n"
            + "    AForm:\n"
            + "      type: object\n"
            + "      properties:\n"
            + "        name:\n"
            + "          type: string\n"
            + "        picture:\n"
            + "          type: string\n"
            + "          format: binary\n",
        result.toYaml());
  }

  @OpenAPITest(value = FilterApp.class, includes = "/api/.*")
  public void shouldIncludes(OpenAPIResult result) {
    assertEquals(
        "openapi: 3.0.1\n"
            + "info:\n"
            + "  title: Filter API\n"
            + "  description: Filter API description\n"
            + "  version: \"1.0\"\n"
            + "paths:\n"
            + "  /api/profile:\n"
            + "    get:\n"
            + "      operationId: getApiProfile\n"
            + "      responses:\n"
            + "        \"200\":\n"
            + "          description: Success\n"
            + "          content:\n"
            + "            application/json:\n"
            + "              schema:\n"
            + "                type: string\n"
            + "  /api/profile/{id}:\n"
            + "    get:\n"
            + "      operationId: getApiProfileId\n"
            + "      parameters:\n"
            + "      - name: id\n"
            + "        in: path\n"
            + "        required: true\n"
            + "        schema:\n"
            + "          type: string\n"
            + "      responses:\n"
            + "        \"200\":\n"
            + "          description: Success\n"
            + "          content:\n"
            + "            application/json:\n"
            + "              schema:\n"
            + "                type: string\n",
        result.toYaml());
  }

  @OpenAPITest(value = FilterApp.class, excludes = "^(?!/api).+")
  public void shouldExcludes(OpenAPIResult result) {
    assertEquals(
        "openapi: 3.0.1\n"
            + "info:\n"
            + "  title: Filter API\n"
            + "  description: Filter API description\n"
            + "  version: \"1.0\"\n"
            + "paths:\n"
            + "  /api/profile:\n"
            + "    get:\n"
            + "      operationId: getApiProfile\n"
            + "      responses:\n"
            + "        \"200\":\n"
            + "          description: Success\n"
            + "          content:\n"
            + "            application/json:\n"
            + "              schema:\n"
            + "                type: string\n"
            + "  /api/profile/{id}:\n"
            + "    get:\n"
            + "      operationId: getApiProfileId\n"
            + "      parameters:\n"
            + "      - name: id\n"
            + "        in: path\n"
            + "        required: true\n"
            + "        schema:\n"
            + "          type: string\n"
            + "      responses:\n"
            + "        \"200\":\n"
            + "          description: Success\n"
            + "          content:\n"
            + "            application/json:\n"
            + "              schema:\n"
            + "                type: string\n",
        result.toYaml());
  }

  @OpenAPITest(value = MvcDaggerApp.class)
  public void shouldParseDaggerController(OpenAPIResult result) {
    assertEquals(
            "openapi: 3.0.1\n"
                    + "info:\n"
                    + "  title: MvcDagger API\n"
                    + "  description: MvcDagger API description\n"
                    + "  version: \"1.0\"\n"
                    + "paths:\n"
                    + "  /welcome:\n"
                    + "    get:\n"
                    + "      operationId: sayHi\n"
                    + "      responses:\n"
                    + "        \"200\":\n"
                    + "          description: Success\n"
                    + "          content:\n"
                    + "            application/json:\n"
                    + "              schema:\n"
                    + "                type: string\n",
            result.toYaml());
  }

  @OpenAPITest(value = MvcRequireApp.class)
  public void shouldParseMvcRequireController(OpenAPIResult result) {
    assertEquals(
            "openapi: 3.0.1\n"
                    + "info:\n"
                    + "  title: MvcRequire API\n"
                    + "  description: MvcRequire API description\n"
                    + "  version: \"1.0\"\n"
                    + "paths:\n"
                    + "  /welcome:\n"
                    + "    get:\n"
                    + "      operationId: sayHi\n"
                    + "      responses:\n"
                    + "        \"200\":\n"
                    + "          description: Success\n"
                    + "          content:\n"
                    + "            application/json:\n"
                    + "              schema:\n"
                    + "                type: string\n",
            result.toYaml());
  }
}
