package engine.core;

public class Command<C, P> {

    private final C command;
    private final P parameters;

    public Command(C command, P parameters) {
        this.command = command;
        this.parameters = parameters;
    }

    public C getCommand() {
        return command;
    }

    public P getParameters() {
        return parameters;
    }
}
