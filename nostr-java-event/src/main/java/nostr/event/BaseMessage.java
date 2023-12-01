package nostr.event;

import nostr.base.IElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

/**
 *
 * @author squirrel
 */
@Data
@AllArgsConstructor
@ToString
public abstract class BaseMessage implements IElement {

    private final String command;

    protected BaseMessage() {
        this.command = null;
    }

    @Override
    public Integer getNip() {
        return 1;
    }

}
