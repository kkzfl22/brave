/*
 * Copyright 2013-2020 The OpenZipkin Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package brave.internal.baggage;

import brave.baggage.BaggageField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class FixedBaggageFieldsFactory extends ExtraBaggageFieldsFactory {
  public static ExtraBaggageFieldsFactory newFactory(List<BaggageField> fields) {
    if (fields == null) throw new NullPointerException("fields == null");
    Map<BaggageField, Integer> initialFieldIndices = new LinkedHashMap<>();
    Object[] array = new Object[fields.size() * 2];
    int i = 0;
    for (BaggageField field : fields) {
      initialFieldIndices.put(field, i);
      array[i] = field;
      i += 2;
    }
    return new FixedBaggageFieldsFactory(array, initialFieldIndices);
  }

  final Object[] initialState;
  final Map<BaggageField, Integer> initialFieldIndices;
  final List<BaggageField> initialFieldList;
  final int initialArrayLength;

  FixedBaggageFieldsFactory(Object[] initialState, Map<BaggageField, Integer> initialFieldIndices) {
    this.initialState =initialState;
    this.initialFieldIndices = Collections.unmodifiableMap(initialFieldIndices);
    this.initialFieldList =
        Collections.unmodifiableList(new ArrayList<>(initialFieldIndices.keySet()));
    this.initialArrayLength = initialState.length;
  }

  @Override public ExtraBaggageFields create() {
    return new ExtraBaggageFields(new FixedBaggageState(this, initialState));
  }

  @Override public ExtraBaggageFields create(ExtraBaggageFields parent) {
    Object[] parentState =
        parent != null ? ((FixedBaggageState) parent.internal).state : initialState;
    return new ExtraBaggageFields(new FixedBaggageState(this, parentState));
  }
}
