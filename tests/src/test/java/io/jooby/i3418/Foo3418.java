/*
 * Jooby https://jooby.io
 * Apache License Version 2.0 https://jooby.io/LICENSE.txt
 * Copyright 2014 Edgar Espina
 */
package io.jooby.i3418;

import io.jooby.Jooby;

public class Foo3418 extends Jooby {
  {
    get("/app", ctx -> getClass().getSimpleName());
  }
}
