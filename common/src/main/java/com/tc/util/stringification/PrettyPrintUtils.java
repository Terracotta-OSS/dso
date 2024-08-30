/*
 * Copyright Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tc.util.stringification;

import com.tc.util.Assert;

/**
 * Contains methods for pretty-printing various things.
 */
public class PrettyPrintUtils {

  public static String pluralize(String base, int quantity) {
    if (quantity == 1) return base;
    else if (base.trim().toLowerCase().endsWith("s")) return base + "es";
    else return base + "s";
  }

  public static String quantity(String ofWhat, int howMany) {
    return "" + howMany + " " + pluralize(ofWhat, howMany);
  }

  public static String percentage(double value, int howManyDecimalDigits) {
    Assert.eval(howManyDecimalDigits >= 0);

    value *= 100.0;

    int integral = howManyDecimalDigits > 0 ? (int) value : (int) Math.round(value);
    int fraction = (int) Math.round(Math.abs(value - integral) * 100.0);

    String integralPart = Integer.toString(integral);
    String fractionPart = Integer.toString(fraction);
    while (fractionPart.length() < howManyDecimalDigits)
      fractionPart = "0" + fractionPart;

    if (howManyDecimalDigits == 0) return integralPart + "%";
    else return integralPart + "." + fractionPart + "%";
  }

}
