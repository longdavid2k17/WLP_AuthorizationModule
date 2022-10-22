package pl.com.kantoch.authorizationmodule.entities.events;

import org.springframework.context.ApplicationEvent;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.user.User;

public class OnPasswordResetRequestEvent extends ApplicationEvent
{
    private User user;

    public OnPasswordResetRequestEvent(User user) {
        super(user);
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
