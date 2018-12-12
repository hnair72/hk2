/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.jvnet.testing.hk2mockito;

import java.io.Closeable;
import javax.inject.Inject;
import static org.assertj.core.api.Assertions.assertThat;
import org.jvnet.testing.hk2mockito.fixture.BasicGreetingService;
import org.jvnet.testing.hk2mockito.fixture.service.ConstructorInjectionGreetingService;
import org.jvnet.testing.hk2testng.HK2;
import org.mockito.Answers;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import org.mockito.internal.util.MockUtil;
import org.mockito.mock.MockCreationSettings;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author Sharmarke Aden
 */
@HK2
public class CustomMockSettingsInjectionTest {

    @SUT
    @Inject
    ConstructorInjectionGreetingService sut;
    @MC(name = "customName", answer = Answers.RETURNS_MOCKS, extraInterfaces = Closeable.class)
    @Inject
    BasicGreetingService collaborator;

    @BeforeClass
    public void verifyInjection() {
        assertThat(sut).isNotNull();
        assertThat(collaborator).isNotNull();
        assertThat(mockingDetails(sut).isSpy()).isTrue();
        assertThat(mockingDetails(collaborator).isMock()).isTrue();
		MockCreationSettings settings = MockUtil.getMockHandler(collaborator).getMockSettings();

        assertThat(settings.getMockName().toString()).isEqualTo("customName");
        assertThat(settings.getDefaultAnswer()).isEqualTo(Answers.RETURNS_MOCKS.get());
        assertThat(settings.getExtraInterfaces()).containsOnly(Closeable.class);
    }

    @BeforeMethod
    public void init() {
        reset(sut, collaborator);
    }

    @Test
    public void callToGreetShouldCallCollboratorGreet() {
        String greeting = "Hello!";
        given(collaborator.greet()).willReturn(greeting);

        String result = sut.greet();

        assertThat(result).isEqualTo(greeting);
        verify(sut).greet();
        verify(collaborator).greet();
    }

}
