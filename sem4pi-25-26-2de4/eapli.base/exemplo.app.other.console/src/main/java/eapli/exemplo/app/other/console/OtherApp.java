/*
 * Copyright (c) 2013-2024 the original author or authors.
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package eapli.exemplo.app.other.console;

import eapli.exemplo.app.common.console.presentation.authz.LoginAction;
import eapli.exemplo.app.other.console.presentation.MainMenu;
import eapli.exemplo.infrastructure.persistence.PersistenceContext;
import eapli.exemplo.usermanagement.domain.ExemploPasswordPolicy;
import eapli.exemplo.usermanagement.domain.ExemploRoles;
import eapli.framework.infrastructure.authz.application.AuthzRegistry;
import eapli.framework.infrastructure.authz.domain.model.PlainTextEncoder;

/**
 *
 * @author Paulo Gandra Sousa
 */
@SuppressWarnings("squid:S106")
public final class OtherApp {

	/**
	 * Empty constructor is private to avoid instantiation of this class.
	 */
	private OtherApp() {
	}

	public static void main(final String[] args) {
		System.out.println("=====================================");
		System.out.println("Other App");
		System.out.println("(C) 2016-2024");
		System.out.println("=====================================");

		AuthzRegistry.configure(PersistenceContext.repositories().users(), new ExemploPasswordPolicy(),
				new PlainTextEncoder());

		// login and go to main menu
		if (new LoginAction(ExemploRoles.OTHER_EXAMPLE).execute()) {
			final var menu = new MainMenu();
			menu.mainLoop();
		}

		// exiting the application, closing all threads
		System.exit(0);
	}
}
