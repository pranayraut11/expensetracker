package com.example.expensetracker.service;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

@Service
public class TagExtractionService {

    private final TokenizerME tokenizer;
    private final POSTaggerME posTagger;
    private final NameFinderME orgFinder;
    private final NameFinderME locationFinder;

    public TagExtractionService() throws IOException {
        tokenizer = new TokenizerME(
            new TokenizerModel(getClass().getResourceAsStream("/models/en-token.bin"))
        );

        posTagger = new POSTaggerME(
            new POSModel(getClass().getResourceAsStream("/models/en-pos-maxent.bin"))
        );

        orgFinder = new NameFinderME(
            new TokenNameFinderModel(getClass().getResourceAsStream("/models/en-ner-organization.bin"))
        );

        locationFinder = new NameFinderME(
            new TokenNameFinderModel(getClass().getResourceAsStream("/models/en-ner-location.bin"))
        );
    }

    public Set<String> extractTags(String description) {
        Set<String> tags = new LinkedHashSet<>();

        String[] tokens = tokenizer.tokenize(description);

        // Add all tokens
        for (String t : tokens) tags.add(t.toLowerCase());

        // Detect nouns (merchant names & places)
        String[] posTags = posTagger.tag(tokens);
        for (int i = 0; i < tokens.length; i++) {
            if (posTags[i].startsWith("NN")) {
                tags.add(tokens[i].toLowerCase());
            }
        }

        // Detect organizations
        for (Span s : orgFinder.find(tokens)) {
            tags.add(tokens[s.getStart()].toLowerCase());
        }

        // Detect locations
        for (Span s : locationFinder.find(tokens)) {
            tags.add(tokens[s.getStart()].toLowerCase());
        }

        return tags;
    }
}
