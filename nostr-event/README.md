# nostr-event

We offer support for selected events and tags out of the box:

The events:
- Event Deletion
- Encrypted Direct Messages
- Ephemeral Events
- Internet Identifier Metadata Events
- Mentions
- Set Metadata events
- Ots Events
- Reaction Events
- Replaceable Events
- Text Note Events

The Tags:
- Deletion Tag
- Event Tag
- Nonce Tag
- Pubkey Tag
- Subject Tag

Additionally, you may use `GenericTag` and `GenericEvent` classes to create your custom tags and events.

## Creating a Custom Tag
Consider the tag syntax:
`[<code>, <attribute value 0>, <attribute value 1>, ..., <attribute value n>]`

- The tag is specified in a NIP
- The tag has a code and zero or more attributes
- An attribute may be specified by another NIP
- The tag is related to a parent event

For illustration purpose, we assume the tag is defined in **NIP 777** and has three attributes, the last one is specified in **NIP-888**

Here is the corresponding java code:

```java
    // Create the attributes
    final ElementAttribute attr0 = ElementAttribute.builder().value(new StringValue("value 0")).build();
    final ElementAttribute attr1 = ElementAttribute.builder().value(new StringValue("value 1")).build();
    final ElementAttribute attr2 = ElementAttribute.builder().value(new StringValue("value 2")).nip(888).build();                        
    
    Set<ElementAttribute> attributes = new HashSet<>();
    attributes.add(attr0);
    attributes.add(attr1);
    attributes.add(attr2);

    GenericTag tag = new GenericTag(777, "code", attributes);
    System.out.println(tag.toString(); //["code", "value 0", "value 1", "value 2"]
    
    TagList tags = new TagList();
    tags.add(tag);
    TextNoteEvent event = new TextNoteEvent(publicKey, tags, "Hello Nostr!");
```

## Creating a custom Event