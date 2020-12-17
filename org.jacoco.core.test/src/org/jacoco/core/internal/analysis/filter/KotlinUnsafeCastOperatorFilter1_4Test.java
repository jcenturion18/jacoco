/*******************************************************************************
 * Copyright (c) 2009, 2020 Mountainminds GmbH & Co. KG and Contributors
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Evgeny Mandrikov - initial API and implementation
 *
 *******************************************************************************/
package org.jacoco.core.internal.analysis.filter;

import org.jacoco.core.internal.instr.InstrSupport;
import org.junit.Test;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class KotlinUnsafeCastOperatorFilter1_4Test extends FilterTestBase {

	private final KotlinUnsafeCastOperatorFilter1_4 filter = new KotlinUnsafeCastOperatorFilter1_4();

	private final MethodNode m = new MethodNode(InstrSupport.ASM_API_VERSION, 0,
			"name", "()V", null, null);

	@Test
	public void should_filter() {
		final Label label = new Label();

		m.visitInsn(Opcodes.DUP);
		m.visitJumpInsn(Opcodes.IFNONNULL, label);
		final AbstractInsnNode expectedFrom = m.instructions.getLast();
		m.visitTypeInsn(Opcodes.NEW, "java/lang/NullPointerException");
		m.visitInsn(Opcodes.DUP);
		m.visitLdcInsn("null cannot be cast to non-null type kotlin.String");
		m.visitMethodInsn(Opcodes.INVOKESPECIAL,
				"java/lang/NullPointerException", "<init>",
				"(Ljava/lang/String;)V", false);
		m.visitInsn(Opcodes.ATHROW);
		final AbstractInsnNode expectedTo = m.instructions.getLast();
		m.visitLabel(label);

		filter.filter(m, context, output);

		assertIgnored(new Range(expectedFrom, expectedTo));
	}
}
