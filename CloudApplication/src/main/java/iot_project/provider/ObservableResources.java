package iot_project.provider;

import java.util.ArrayList;
import java.util.List;
import org.springframework.shell.CompletionContext;
import org.springframework.shell.CompletionProposal;
import org.springframework.core.MethodParameter;
import org.springframework.shell.standard.ValueProviderSupport;
import org.springframework.stereotype.Component;
import iot_project.contiki.AvailableResources;

@Component
public class ObservableResources extends ValueProviderSupport {
    @Override
    public List<CompletionProposal> complete(MethodParameter methodParameter,
            CompletionContext completionContext, String[] strings) {

        List<CompletionProposal> result = new ArrayList<CompletionProposal>();
        String userInput = completionContext.currentWordUpToCursor();

        AvailableResources.getInstance().stream()
                .filter(t -> t.isObservable() && t.toFormattedString().contains(userInput))
                .forEach(t -> result.add(new CompletionProposal(t.toFormattedString())));

        return result;
    }
}
