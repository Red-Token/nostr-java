package nostr.test.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.java.Log;
import nostr.api.NIP01;
import nostr.api.NIP04;
import nostr.api.NIP15;
import nostr.api.NIP32;
import nostr.api.NIP44;
import nostr.api.NIP57;
import nostr.base.ElementAttribute;
import nostr.base.PrivateKey;
import nostr.base.PublicKey;
import nostr.crypto.bech32.Bech32;
import nostr.crypto.bech32.Bech32Prefix;
import nostr.event.BaseTag;
import nostr.event.impl.CreateOrUpdateStallEvent;
import nostr.event.impl.CreateOrUpdateStallEvent.Stall;
import nostr.event.impl.DirectMessageEvent;
import nostr.event.impl.EncryptedPayloadEvent;
import nostr.event.impl.NostrMarketplaceEvent;
import nostr.event.impl.NostrMarketplaceEvent.Product.Spec;
import nostr.event.impl.TextNoteEvent;
import nostr.event.impl.ZapReceiptEvent;
import nostr.event.impl.ZapRequestEvent;
import nostr.id.Identity;
import nostr.util.NostrException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author eric
 */
@Log
public class ApiEventTest {

    public static final String NOSTR_JAVA_PUBKEY = "56adf01ca1aa9d6f1c35953833bbe6d99a0c85b73af222e6bd305b51f2749f6f";

    private static final Map<String, String> RELAYS = getRelays();

    @Test
    public void testNIP01CreateTextNoteEvent() {
        log.info("testNIP01CreateTextNoteEvent");

        PublicKey publicKey = new PublicKey("");
        var recipient = NIP01.createPubKeyTag(publicKey);
        List<BaseTag> tags = new ArrayList<>();
        tags.add(recipient);
        Identity identity = Identity.generateRandomIdentity();
        var nip01 = new NIP01<TextNoteEvent>(identity);
		var instance = nip01.createTextNoteEvent(tags, "Hello simplified nostr-java!")
				.getEvent();
        instance.update();

        assertNotNull(instance.getId());
        assertNotNull(instance.getCreatedAt());
        assertNull(instance.getSignature());

        final String bech32 = instance.toBech32();
        assertNotNull(bech32);
        assertDoesNotThrow(() -> {
            assertEquals(Bech32Prefix.NOTE.getCode(), Bech32.decode(bech32).hrp);
        });
    }

    @Test
    public void testNIP01SendTextNoteEvent() {
        System.out.println("testNIP01SendTextNoteEvent");

        Identity identity = Identity.generateRandomIdentity();
        var nip01 = new NIP01<TextNoteEvent>(identity);
		var instance = nip01.createTextNoteEvent("Hello simplified nostr-java!")
        		.sign();

        var signature = instance.getEvent().getSignature();
        assertNotNull(signature);
        instance.setRelays(RELAYS).send();

        //Assertions.assertNotNull(instance.responses());
        //Assertions.assertFalse(instance.responses().isEmpty());
        //instance.responses().forEach(System.out::println);
    }

    @Test
    public void testNIP04SendDirectMessage() {
        System.out.println("testNIP04SendDirectMessage");

        PublicKey nostr_java = new PublicKey(NOSTR_JAVA_PUBKEY);
        Identity identity = Identity.generateRandomIdentity();
        var nip04 = new NIP04<DirectMessageEvent>(identity, nostr_java);
        var instance = nip04.createDirectMessageEvent("Quand on n'a que l'amour pour tracer un chemin et forcer le destin...")
        		.sign();
        
        var signature = instance.getEvent().getSignature();
        assertNotNull(signature);
        instance.setRelays(RELAYS).send();
    }

    @Test
    public void testNIP44SendDirectMessage() {
        System.out.println("testNIP44SendDirectMessage");

        PublicKey nostr_java = new PublicKey(NOSTR_JAVA_PUBKEY);

        Identity identity = Identity.generateRandomIdentity();
        var nip44 = new NIP44<EncryptedPayloadEvent>(identity, nostr_java);

        var instance = nip44.createDirectMessageEvent("Quand on n'a que l'amour pour tracer un chemin et forcer le destin...").sign();
        assertNotNull(instance.getEvent().getSignature());
        instance.setRelays(RELAYS).send();
    }

    @Test
    public void testNIP04EncryptDecrypt() {
        System.out.println("testNIP04EncryptDecrypt");

        var nostr_java = new PublicKey(NOSTR_JAVA_PUBKEY);
        Identity identity = Identity.generateRandomIdentity();
        var nip04 = new NIP04<DirectMessageEvent>(identity, nostr_java);
        var instance = nip04.createDirectMessageEvent("Quand on n'a que l'amour pour tracer un chemin et forcer le destin...")
		        .sign();

        var message = NIP04.decrypt(identity, instance.getEvent());

        assertEquals("Quand on n'a que l'amour pour tracer un chemin et forcer le destin...", message);
    }

    @Test
    public void testNIP44EncryptDecrypt() {
        System.out.println("testNIP44EncryptDecrypt");

        var nostr_java = new PublicKey(NOSTR_JAVA_PUBKEY);

        Identity identity = Identity.generateRandomIdentity();
        var nip44 = new NIP44<EncryptedPayloadEvent>(identity, nostr_java);

        var instance = nip44.createDirectMessageEvent("Quand on n'a que l'amour pour tracer un chemin et forcer le destin...").sign();
        var message = NIP44.decrypt(identity, instance.getEvent());

        assertEquals("Quand on n'a que l'amour pour tracer un chemin et forcer le destin...", message);
    }

    @Test
    public void testNIP15CreateStallEvent() {
        System.out.println("testNIP15CreateStallEvent");

        Stall stall = createStall();
        var nip15 = new NIP15<>(Identity.create(PrivateKey.generateRandomPrivKey()));

        // Create and send the nostr event
        var instance = nip15.createCreateOrUpdateStallEvent(stall).sign();
        var signature = instance.getEvent().getSignature();
        assertNotNull(signature);

        // Fetch the content and compare with the above original
        var content = instance.getEvent().getContent();
        ObjectMapper mapper = new ObjectMapper();
        assertDoesNotThrow(() -> {
            Stall expected = mapper.readValue(content, Stall.class);
            assertEquals(expected, stall);
        });



    }

    @Test
    public void testNIP15UpdateStallEvent() {
        System.out.println("testNIP15UpdateStallEvent");

        var stall = createStall();
        var nip15 = new NIP15<>(Identity.create(PrivateKey.generateRandomPrivKey()));

        // Create and send the nostr event
        var instance = nip15.createCreateOrUpdateStallEvent(stall).sign();
        var signature = instance.getEvent().getSignature();
        assertNotNull(signature);
        nip15.setRelays(RELAYS).send();

        // Update the shipping
        var shipping = stall.getShipping();
        shipping.setCost(20.00f);
        nip15.createCreateOrUpdateStallEvent(stall).sign().setRelays(RELAYS).send();
    }

    @Test
    public void testNIP15CreateProductEvent() {

        System.out.println("testNIP15CreateProductEvent");

        // Create the stall object
        var stall = createStall();
        var nip15 = new NIP15<>(Identity.create(PrivateKey.generateRandomPrivKey()));

        // Create the product
        var product = createProduct(stall);

        List<String> categories = new ArrayList<>();
        categories.add("bijoux");
        categories.add("Hommes");

        nip15.createCreateOrUpdateProductEvent(product, categories).sign().setRelays(RELAYS).send();
    }

    @Test
    public void testNIP15UpdateProductEvent() {

        System.out.println("testNIP15UpdateProductEvent");

        // Create the stall object
        var stall = createStall();
        var nip15 = new NIP15<>(Identity.create(PrivateKey.generateRandomPrivKey()));

        // Create the product
        var product = createProduct(stall);

        List<String> categories = new ArrayList<>();
        categories.add("bijoux");
        categories.add("Hommes");

        nip15.createCreateOrUpdateProductEvent(product, categories).sign().setRelays(RELAYS).send();

        product.setDescription("Un nouveau bijou en or");
        categories.add("bagues");

        nip15.sign().setRelays(RELAYS).send();
    }

    @Test
    public void testNIP32CreateNameSpace() {
        
        System.out.println("testNIP32CreateNameSpace");
        
        var langNS = NIP32.createNameSpaceTag("Languages");
        
        assertEquals("L", langNS.getCode());
        assertEquals(1, langNS.getAttributes().size());
        assertEquals("Languages", langNS.getAttributes().iterator().next().getValue());
    }
    
    @Test
    public void testNIP32CreateLabel1() {

        System.out.println("testNIP32CreateLabel1");
                
        var label = NIP32.createLabelTag("Languages", "english");
        
        assertEquals("l", label.getCode());
        assertEquals(2, label.getAttributes().size());
        assertTrue(label.getAttributes().contains(new ElementAttribute("param0", "english", 32)));
        assertTrue(label.getAttributes().contains(new ElementAttribute("param1", "Languages", 32)));
    }

    @Test
    public void testNIP32CreateLabel2() {

        System.out.println("testNIP32CreateLabel2");
                
        var metadata = new HashMap<String, Object>();
        metadata.put("article", "the");
        var label = NIP32.createLabelTag("Languages", "english", metadata);
        
        assertEquals("l", label.getCode());
        assertEquals(3, label.getAttributes().size());
        assertTrue(label.getAttributes().contains(new ElementAttribute("param0", "english", 32)));
        assertTrue(label.getAttributes().contains(new ElementAttribute("param1", "Languages", 32)));
        assertTrue(label.getAttributes().contains(new ElementAttribute("param2", "{\\\"article\\\":\\\"the\\\"}", 32)), "{\\\"article\\\":\\\"the\\\"}");
    }

    @Test
    void testNIP57CreateZapRequestEvent() {
        System.out.println("testNIP57CreateZapRequestEvent");

        Identity sender = Identity.generateRandomIdentity();
        List<BaseTag> baseTags = new ArrayList<BaseTag>();
        PublicKey recipient = Identity.generateRandomIdentity().getPublicKey();
        var nip57 = new NIP57<ZapRequestEvent>(sender);
        final String ZAP_REQUEST_CONTENT = "zap request content";
        final Long AMOUNT = 1232456L;
        final String LNURL = "lnUrl";
        final String RELAYS_TAG = "ws://localhost:5555";
        ZapRequestEvent instance = nip57.createZapRequestEvent(recipient, baseTags, ZAP_REQUEST_CONTENT, AMOUNT, LNURL, RELAYS_TAG).getEvent();
        instance.update();

        assertNotNull(instance.getId());
        assertNotNull(instance.getCreatedAt());
        assertNotNull(instance.getContent());
        assertNull(instance.getSignature());

        assertNotNull(instance.getZapRequest());
        assertNotNull(instance.getZapRequest().getRelaysTag());
        assertNotNull(instance.getZapRequest().getAmount());
        assertNotNull(instance.getZapRequest().getLnUrl());

        assertEquals(ZAP_REQUEST_CONTENT, instance.getContent());
        assertTrue(instance.getZapRequest().getRelaysTag().getRelays().stream().anyMatch(relay -> relay.getUri().equals(RELAYS_TAG)));
        assertEquals(AMOUNT, instance.getZapRequest().getAmount());
        assertEquals(LNURL, instance.getZapRequest().getLnUrl());

        final String bech32 = instance.toBech32();
        assertNotNull(bech32);
        assertDoesNotThrow(() -> {
            assertEquals(Bech32Prefix.NOTE.getCode(), Bech32.decode(bech32).hrp);
        });
    }

    @Test
    void testNIP57CreateZapReceiptEvent() {
        System.out.println("testNIP57CreateZapReceiptEvent");

        Identity sender = Identity.generateRandomIdentity();
        List<BaseTag> baseTags = new ArrayList<BaseTag>();
        String zapRequestPubKeyTag = Identity.generateRandomIdentity().getPublicKey().toString();
        String zapRequestEventTag = Identity.generateRandomIdentity().getPublicKey().toString();
        String zapRequestAddressTag = Identity.generateRandomIdentity().getPublicKey().toString();
        final String ZAP_RECEIPT_IDENTIFIER = "ipsum";
        final String ZAP_RECEIPT_RELAY_URI = "ws://localhost:5555";
        final String BOLT_11 = "bolt11";
        final String DESCRIPTION_SHA256 = "descriptionSha256";
        final String PRE_IMAGE = "preimage";
        var nip57 = new NIP57<ZapReceiptEvent>(sender);

        ZapReceiptEvent instance = nip57.createZapReceiptEvent(zapRequestPubKeyTag, baseTags, zapRequestEventTag, zapRequestAddressTag, ZAP_RECEIPT_IDENTIFIER, ZAP_RECEIPT_RELAY_URI, BOLT_11, DESCRIPTION_SHA256, PRE_IMAGE).getEvent();
        instance.update();

        assertNotNull(instance.getId());
        assertNotNull(instance.getCreatedAt());
        assertNull(instance.getSignature());


        assertNotNull(instance.getZapReceipt());
        assertNotNull(instance.getZapReceipt().getBolt11());
        assertNotNull(instance.getZapReceipt().getDescriptionSha256());
        assertNotNull(instance.getZapReceipt().getPreimage());

        assertEquals(BOLT_11, instance.getZapReceipt().getBolt11());
        assertEquals(DESCRIPTION_SHA256, instance.getZapReceipt().getDescriptionSha256());
        assertEquals(PRE_IMAGE, instance.getZapReceipt().getPreimage());

        final String bech32 = instance.toBech32();
        assertNotNull(bech32);
        assertDoesNotThrow(() -> {
            assertEquals(Bech32Prefix.NOTE.getCode(), Bech32.decode(bech32).hrp);
        });
    }

    private Stall createStall() {

        // Create the county list
        List<String> countries = new ArrayList<>();
        countries.add("France");
        countries.add("Canada");
        countries.add("Cameroun");

        // Create the shipping object
        var shipping = new CreateOrUpdateStallEvent.Stall.Shipping();
        shipping.setCost(12.00f);
        shipping.setCountries(countries);
        shipping.setName("French Countries");

        // Create the stall object
        var stall = new CreateOrUpdateStallEvent.Stall();
        stall.setCurrency("USD");
        stall.setDescription("This is a test stall");
        stall.setName("Maximus Primus");
        stall.setShipping(shipping);

        return stall;
    }

    private NostrMarketplaceEvent.Product createProduct(Stall stall) {

        // Create the product
        var product = new NostrMarketplaceEvent.Product();
        product.setCurrency("USD");
        product.setDescription("Un bijou en or");
        product.setImages(new ArrayList<>());
        product.setName("Bague");
        product.setPrice(450.00f);
        product.setQuantity(4);
        List<Spec> specs = new ArrayList<>();
        specs.add(new Spec("couleur", "or"));
        specs.add(new Spec("poids", "150g"));
        product.setSpecs(specs);
        product.setStall(stall);

        return product;
    }

    private static Map<String, String> getRelays() {
        Map<String, String> relays = new HashMap<>();
        Properties properties = new Properties();
        try {
            InputStream is = ApiEventTest.class.getClassLoader().getResourceAsStream("relays.properties");
            if (is != null) {
                properties.load(is);
                for (String key : properties.stringPropertyNames()) {
                    relays.put(key, properties.getProperty(key));
                }
            } else {
                throw new RuntimeException("Unable to find 'relays.properties' in the classpath");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return relays;
    }
}
