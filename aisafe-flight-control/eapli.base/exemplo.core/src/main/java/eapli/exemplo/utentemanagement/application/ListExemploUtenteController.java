/*
 * Copyright (c) 2013-2024 the original author or authors.
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package eapli.exemplo.utentemanagement.application;

import eapli.exemplo.infrastructure.persistence.PersistenceContext;
import eapli.exemplo.usermanagement.domain.ExemploRoles;
import eapli.exemplo.utentemanagement.domain.Utente;
import eapli.exemplo.utentemanagement.repositories.UtenteRepository;
import eapli.framework.infrastructure.authz.application.AuthorizationService;
import eapli.framework.infrastructure.authz.application.AuthzRegistry;

/**
 *
 * @author losa
 */
public class ListExemploUtenteController {
    private final AuthorizationService authz = AuthzRegistry.authorizationService();

    private final UtenteRepository repo = PersistenceContext.repositories().utentes();

    public Iterable<Utente> activeExemploUtente() {
        authz.ensureAuthenticatedUserHasAnyOf(ExemploRoles.POWER_USER, ExemploRoles.ADMIN);

        return this.repo.findAllActive();
    }
}
