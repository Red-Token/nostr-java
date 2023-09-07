package nostr.event.json.codec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import nostr.base.IDecoder;
import nostr.event.impl.Filters;

/**
 *
 * @author eric
 */
@Data
@AllArgsConstructor
public class FiltersDecoder implements IDecoder<Filters> {

    private final String jsonString;

    @Override
    public Filters decode()  {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonString, Filters.class);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }

}
