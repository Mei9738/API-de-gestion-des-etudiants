package org.example.project2.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class Messages {

    private final MessageSource messageSource;

    private MessageSourceAccessor accessor;

    @PostConstruct
    private void init() {
        accessor = new MessageSourceAccessor(messageSource, Locale.ENGLISH);
    }

    public String get(String code, Object... params) {
        return accessor.getMessage(code, params);
    }

    public String get(String code, Object[] params, Locale locale) {
        return accessor.getMessage(code, params, locale);
    }
}

