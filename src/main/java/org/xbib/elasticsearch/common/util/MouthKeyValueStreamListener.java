/*
 * Copyright (C) 2014 Jörg Prante
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.xbib.elasticsearch.common.util;

import org.xbib.elasticsearch.jdbc.strategy.Mouth;

import java.io.IOException;

/**
 * This class consumes pairs from a key/value stream
 * and transports them to the mouth.
 */
public class MouthKeyValueStreamListener<K, V> extends PlainKeyValueStreamListener<K, V> {

    private Mouth output;

    public MouthKeyValueStreamListener<K, V> output(Mouth output) {
        this.output = output;
        return this;
    }

    public MouthKeyValueStreamListener<K, V> shouldIgnoreNull(boolean shouldIgnoreNull) {
        super.shouldIgnoreNull(shouldIgnoreNull);
        return this;
    }

    /**
     * The object is complete. Push it to the mouth.
     *
     * @param object the object
     * @return this value listener
     * @throws java.io.IOException if this method fails
     */
    public MouthKeyValueStreamListener<K, V> end(IndexableObject object) throws IOException {
        if (object.isEmpty()) {
            return this;
        }
        if (output != null) {
            if (object.optype() == null) {
                output.index(object, false);
            } else if ("index".equals(object.optype())) {
                output.index(object, false);
            } else if ("create".equals(object.optype())) {
                output.index(object, true);
            } else if ("delete".equals(object.optype())) {
                output.delete(object);
            } else {
                throw new IllegalArgumentException("unknown optype: " + object.optype());
            }
        }
        return this;
    }

}
