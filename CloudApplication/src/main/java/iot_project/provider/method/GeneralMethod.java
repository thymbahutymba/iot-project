package iot_project.provider.method;

import iot_project.contiki.AvailableResources;
import iot_project.contiki.Method;
import java.util.ArrayList;
import java.util.List;
import org.springframework.core.MethodParameter;
import org.springframework.shell.CompletionContext;
import org.springframework.shell.CompletionProposal;
import org.springframework.shell.standard.ValueProviderSupport;

public class GeneralMethod extends ValueProviderSupport {
    //private String method;
//
    //public GeneralMethod(String method) {
    //    this.method = method.toUpperCase();
    //}

    private Method method;

    public GeneralMethod(Method m) {
        this.method = m;
    }

    @Override
    public List<CompletionProposal> complete(MethodParameter methodParameter,
            CompletionContext completionContext, String[] strings) {

        List<CompletionProposal> result = new ArrayList<CompletionProposal>();
        String userInput = completionContext.currentWordUpToCursor();

        AvailableResources.getInstance().stream()
                .filter(t -> t.hasMethod(this.method) && t.toFormattedString().contains(userInput))
                .forEach(t -> result.add(new CompletionProposal(t.toFormattedString())));

        return result;
    }
}
