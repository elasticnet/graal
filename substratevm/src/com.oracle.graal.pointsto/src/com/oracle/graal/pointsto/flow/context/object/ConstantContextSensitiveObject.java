/*
 * Copyright (c) 2013, 2017, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package com.oracle.graal.pointsto.flow.context.object;

import com.oracle.graal.pointsto.BigBang;
import com.oracle.graal.pointsto.meta.AnalysisType;

import jdk.vm.ci.meta.JavaConstant;

/**
 * A context sensitive analysis object that represents a constant. The context for this analysis
 * object is the constant it wraps.
 */
public class ConstantContextSensitiveObject extends ContextSensitiveAnalysisObject {

    /**
     * The wrapped {@link JavaConstant constant}. If null then this is the merged constant object
     * for its type.
     */
    private final JavaConstant constant;

    /**
     * Constructor used for the merged constant object, i.e., after the number of individual
     * constant objects for a type has reached the maximum number of recorded constants threshold.
     */
    public ConstantContextSensitiveObject(BigBang bb, AnalysisType type) {
        this(bb, type, null);
    }

    public ConstantContextSensitiveObject(BigBang bb, AnalysisType type, JavaConstant constant) {
        super(bb.getUniverse(), type, AnalysisObjectKind.ConstantContextSensitive);
        assert bb.trackConcreteAnalysisObjects(type);
        this.constant = constant;
    }

    public JavaConstant getConstant() {
        return constant;
    }

    /** Test if this is the merged constant object for its type. */
    public boolean isMergedConstantObject() {
        // If the constant field is null then this is the merged constant object for its type.
        return constant == null;
    }

    /** The object has been in contact with an context insensitive object in an union operation. */
    @Override
    public void noteMerge(BigBang bb) {
        assert bb.analysisPolicy().isMergingEnabled();

        if (!merged) {
            if (!isEmptyObjectArrayConstant(bb)) {
                /*
                 * An empty array constant doesn't have any values and cannot be written to. We
                 * don't want to merge it with the other arrays of the same type and thus falsely
                 * reflect their the element values.
                 */
                super.noteMerge(bb);
            }
        }
    }

    @Override
    public boolean isEmptyObjectArrayConstant(BigBang bb) {
        if (this.isMergedConstantObject()) {
            return false;
        }

        return AnalysisObject.isEmptyObjectArrayConstant(bb, getConstant());
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(super.toString()).append("  ");
        if (constant == null) {
            result.append("MERGED CONSTANT");
        } else {
            // result.append(constant);
        }
        return result.toString();
    }
}
