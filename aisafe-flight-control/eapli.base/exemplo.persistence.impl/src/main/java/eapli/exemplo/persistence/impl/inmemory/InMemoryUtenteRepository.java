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
package eapli.exemplo.persistence.impl.inmemory;

import java.util.Optional;

import eapli.exemplo.utentemanagement.domain.MecanographicNumber;
import eapli.exemplo.utentemanagement.domain.Utente;
import eapli.exemplo.utentemanagement.repositories.UtenteRepository;
import eapli.framework.infrastructure.authz.domain.model.Username;
import eapli.framework.infrastructure.repositories.impl.inmemory.InMemoryDomainRepository;

/**
 *
 * @author Jorge Santos ajs@isep.ipp.pt 02/04/2016
 */
public class InMemoryUtenteRepository extends InMemoryDomainRepository<Utente, MecanographicNumber>
		implements UtenteRepository {

	static {
		InMemoryInitializer.init();
	}

	@Override
	public Optional<Utente> findByUsername(final Username name) {
		return matchOne(e -> e.user().username().equals(name));
	}

	@Override
	public Optional<Utente> findByMecanographicNumber(final MecanographicNumber number) {
		return Optional.of(data().get(number));
	}

	@Override
	public Iterable<Utente> findAllActive() {
		return match(e -> e.user().isActive());
	}
}
