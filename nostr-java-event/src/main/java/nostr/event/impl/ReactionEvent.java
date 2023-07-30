package nostr.event.impl;

import java.util.List;
import nostr.event.Kind;
import nostr.base.PublicKey;
import nostr.event.Reaction;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.java.Log;
import nostr.base.IEvent;
import nostr.base.annotation.Event;
import nostr.event.BaseTag;
import nostr.event.tag.EventTag;
import nostr.event.tag.PubKeyTag;

/**
 *
 * @author squirrel
 */
@Data
@Log
@EqualsAndHashCode(callSuper = false)
@Event(name = "Reactions", nip = 25)
public class ReactionEvent extends GenericEvent {

    public ReactionEvent(PublicKey pubKey, List<? extends BaseTag> tags, Reaction reaction, GenericEvent sourceEvent) {
        super(pubKey, Kind.REACTION, tags, reaction.getEmoji());
    }

    public ReactionEvent(PublicKey pubKey, GenericEvent event, Reaction reaction) {
        this(pubKey, event, reaction.getEmoji());
    }

    public ReactionEvent(PublicKey pubKey, GenericEvent event, String reaction) {
        super(pubKey, Kind.REACTION);
        this.setContent(reaction);
        this.addTag(EventTag.builder().idEvent(event.getId()).build());
        this.addTag(PubKeyTag.builder().publicKey(event.getPubKey()).build());
    }
}
