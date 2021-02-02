package com.comviva.interop.txnengine.repositiry.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.comviva.interop.txnengine.entities.Languages;
import com.comviva.interop.txnengine.repositories.LanguagesRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LanguageRepositoryTests {

	@Test
	public void getLanguageRepositoryTest() {
		LanguagesRepository languageRepository = mock(LanguagesRepository.class);
		List<Languages> value =new ArrayList<>();
		Languages languages = new Languages();
		languages.setLangCode("en");
		languages.setLanguage("English");
		value.add(languages);
		when(languageRepository.getAllLanguages()).thenReturn(value);
		assertThat(value.size(), is(languageRepository.getAllLanguages().size()));
		assertThat(value.get(0).getLangCode(), is(languageRepository.getAllLanguages().get(0).getLangCode()));
		assertThat(value.get(0).getLanguage(), is(languageRepository.getAllLanguages().get(0).getLanguage()));
	}
}
