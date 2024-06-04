package annotation.sample;

import jakarta.inject.Inject;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MessageService {

  private final Map<String, MessageSender> messageSenders;

  @Inject
  public MessageService(final Map<String, MessageSender> messageSenders) {
    this.messageSenders = messageSenders;
  }

  public MessageSender getMessageSender(final String name) {
    return messageSenders.get(name);
  }
  public void println (){
    messageSenders.forEach((name, sender) -> {
      System.out.println(name);
      System.out.println(sender);
    });
  }
}
