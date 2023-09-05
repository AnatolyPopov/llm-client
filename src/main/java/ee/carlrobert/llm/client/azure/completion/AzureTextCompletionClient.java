package ee.carlrobert.llm.client.azure.completion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.llm.client.azure.AzureClient;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;
import ee.carlrobert.llm.client.openai.completion.text.OpenAITextCompletionEventSourceListener;
import ee.carlrobert.llm.client.openai.completion.text.request.OpenAITextCompletionRequest;
import ee.carlrobert.llm.completion.CompletionEventListener;
import ee.carlrobert.llm.completion.CompletionEventSourceListener;
import ee.carlrobert.llm.completion.CompletionRequest;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;

public class AzureTextCompletionClient extends AzureCompletionClient {

  public AzureTextCompletionClient(AzureClient client) {
    super(client, "/openai/deployments/%s/completions?api-version=%s");
  }

  @Override
  public EventSource stream(CompletionRequest completionRequest, CompletionEventListener completionEventListener) {
    return EventSources.createFactory(client.getHttpClient())
        .newEventSource(buildHttpRequest((OpenAITextCompletionRequest) completionRequest), getEventSourceListener(completionEventListener));
  }

  @Override
  protected CompletionEventSourceListener getEventSourceListener(CompletionEventListener listeners) {

    return new OpenAITextCompletionEventSourceListener(listeners) {
      @Override
      protected ErrorDetails getErrorDetails(String data) throws JsonProcessingException {
        return new ObjectMapper().readValue(data, AzureApiResponseError.class).getError();
      }
    };
  }
}
