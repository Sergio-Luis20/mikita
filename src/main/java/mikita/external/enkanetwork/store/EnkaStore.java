package mikita.external.enkanetwork.store;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import mikita.exception.ReadException;
import mikita.exception.URLException;
import mikita.external.RawFile;
import mikita.external.github.GitHubRawFile;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class EnkaStore {

    private Map<StoreFilePath, Map<String, ?>> cache;
    private ObjectMapper objectMapper;

    public EnkaStore(ObjectMapper objectMapper) {
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper");
        this.cache = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    public <T> Map<String, T> getData(StoreFilePath storeFilePath) throws ReadException {
        try {
            Map<String, T> data;
            if (cache.containsKey(storeFilePath)) {
                data = (Map<String, T>) cache.get(storeFilePath);
            } else {
                RawFile rawFile = getRawFile(storeFilePath);
                TypeReference<Map<String, T>> mapRef = storeFilePath.getTypeReference();
                data = objectMapper.readValue(rawFile.getInputStream(), mapRef);
                cache.put(storeFilePath, data);
            }
            return data;
        } catch (Exception e) {
            throw new ReadException("Unable to parse " + storeFilePath.getFileName(), e);
        }
    }

    public GitHubRawFile getRawFile(StoreFilePath storeFilePath) throws URLException {
        try {
            return new GitHubRawFile("EnkaNetwork", "API-docs",
                    "store/" + storeFilePath.getFileName());
        } catch (MalformedURLException e) {
            throw new URLException("Could not create proper URL for " + storeFilePath, e);
        }
    }

    public interface StoreFilePath {
        String getFileName();
        <T> TypeReference<Map<String, T>> getTypeReference();
    }

    @Getter
    @AllArgsConstructor
    public enum StoreFile implements StoreFilePath {

        AFFIXES("affixes.json", new TypeReference<Map<String, Affix>>() {}),
        CHARACTERS("characters.json", new TypeReference<Map<String, AvatarData>>() {}),
        LOC("loc.json", new TypeReference<Map<String, Loc>>() {}),
        NAMECARDS("namecards.json", new TypeReference<Map<String, Object>>() {}),
        PFPS("pfps.json", new TypeReference<Map<String, Object>>() {});

        private final String fileName;
        private final TypeReference<?> typeReference;

        @SuppressWarnings("unchecked")
        public <T> TypeReference<Map<String, T>> getTypeReference() {
            return (TypeReference<Map<String, T>>) typeReference;
        }

        public static StoreFile of(String fileName) {
            for (StoreFile storeFile : StoreFile.values()) {
                if (storeFile.fileName.equals(fileName)) {
                    return storeFile;
                }
            }
            return null;
        }

    }

    @Getter
    public enum StoreHsrFile implements StoreFilePath {

        HONKER_AVATARS("honker_avatars.json", new TypeReference<Map<String, Object>>() {}),
        HONKER_CHARACTERS("honker_characters.json", new TypeReference<Map<String, Object>>() {}),
        HONKER_META("honker_meta.json", new TypeReference<Map<String, Object>>() {}),
        HONKER_RANKS("honker_ranks.json", new TypeReference<Map<String, Object>>() {}),
        HONKER_RELICS("honker_relics.json", new TypeReference<Map<String, Object>>() {}),
        HONKER_SKILLS("honker_skills.json", new TypeReference<Map<String, Object>>() {}),
        HONKER_SKILLTREE("honker_skilltree.json", new TypeReference<Map<String, Object>>() {}),
        HONKER_WEPS("honker_weps.json", new TypeReference<Map<String, Object>>() {}),
        HSR("hsr.json", new TypeReference<Map<String, Object>>() {});

        private final String fileName;
        private final TypeReference<?> typeReference;

        StoreHsrFile(String fileName, TypeReference<?> typeReference) {
            this.fileName = "hsr/" + fileName;
            this.typeReference = typeReference;
        }

        @SuppressWarnings("unchecked")
        public <T> TypeReference<Map<String, T>> getTypeReference() {
            return (TypeReference<Map<String, T>>) typeReference;
        }

        public static StoreHsrFile of(String fileName) {
            for (StoreHsrFile storeHsrFile : StoreHsrFile.values()) {
                if (storeHsrFile.fileName.equals(fileName)) {
                    return storeHsrFile;
                }
            }
            return null;
        }

    }

}
