package com.example.commentservice.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.Objects;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new PageableHandlerMethodArgumentResolver());
    }

    private static class PageableHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

        private static final int DEFAULT_PAGE = 0;
        private static final int DEFAULT_SIZE = 10;

        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            return parameter.getParameterAnnotation(RequestParam.class) != null && (
                    Objects.equals(parameter.getParameterName(), "page") || Objects.equals(parameter.getParameterName(), "size"));
        }

        @Override
        public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                      NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
            String parameterName = parameter.getParameterName();
            String parameterValue = webRequest.getParameter(parameterName);

            if (parameterValue == null) {
                if (parameterName.equals("page")) {
                    return DEFAULT_PAGE;
                } else if (parameterName.equals("size")) {
                    return DEFAULT_SIZE;
                }
            }

            return Integer.parseInt(parameterValue);
        }
    }
}
