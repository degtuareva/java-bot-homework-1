package org.homework.services;

import org.homework.api.AiService;
import org.homework.di.annotations.Register;
import org.homework.di.annotations.Resolve;

@Register
public class WebAiService implements AiService {

    @Resolve
    private WebLlMAgent agent;

    @Override
    public String askJavaAssistant(String question) {
        return agent.answerWithLlm(question);
    }
}
