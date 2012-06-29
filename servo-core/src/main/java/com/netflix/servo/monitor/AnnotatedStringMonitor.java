/*
 * #%L
 * servo
 * %%
 * Copyright (C) 2011 - 2012 Netflix
 * %%
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
 * #L%
 */
package com.netflix.servo.monitor;

import com.google.common.base.Objects;
import com.google.common.base.Throwables;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Wraps an annotated field and exposes it as a monitor object.
 */
class AnnotatedStringMonitor extends AbstractMonitor<String> {

    private final Object obj;
    private final AccessibleObject field;

    AnnotatedStringMonitor(MonitorConfig config, Object obj, AccessibleObject field) {
        super(config);
        this.obj = obj;
        this.field = field;
    }

    /** {@inheritDoc} */
    @Override
    public String getValue() {
        Object v = null;
        try {
            field.setAccessible(true);
            if (field instanceof Field) {
                v =  ((Field) field).get(obj);
            } else {
                v = ((Method) field).invoke(obj);
            }
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
        return (v == null) ? null : v.toString();
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof AnnotatedStringMonitor)) {
            return false;
        }
        AnnotatedStringMonitor m = (AnnotatedStringMonitor) obj;
        return config.equals(m.getConfig()) && field.equals(m.field);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Objects.hashCode(config, field);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("config", config)
                .add("field", field)
                .toString();
    }
}