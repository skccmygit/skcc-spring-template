package skcc.arch.app.context;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import skcc.arch.app.util.AuthUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class CaffeineContextStorageServiceTest {

    private CaffeineContextStorageService contextStorageService;

    @Mock
    private AuthUtil authUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Cache<String, Map<String, Object>> cache = Caffeine.newBuilder()
                .expireAfterAccess(30, TimeUnit.MINUTES)
                .maximumSize(1000)
                .build();
        contextStorageService = new CaffeineContextStorageService(cache, authUtil);
    }

    @Test
    void testSetAndGetWithDTO() {
        String uid = "user123";
        when(authUtil.getUID()).thenReturn(uid);

        TestDTO dto = new TestDTO("testValue");

        contextStorageService.set("dtoKey", dto);
        TestDTO retrieved = contextStorageService.get("dtoKey", TestDTO.class);

        assertNotNull(retrieved);
        assertEquals(dto.getValue(), retrieved.getValue());
    }

    @Test
    void testSetAndGetWithListOfDTO() {
        String uid = "user123";
        when(authUtil.getUID()).thenReturn(uid);

        List<TestDTO> dtoList = List.of(new TestDTO("value1"), new TestDTO("value2"));

        contextStorageService.set("dtoListKey", dtoList);
        List retrievedList = contextStorageService.get("dtoListKey", List.class);

        assertNotNull(retrievedList);
        assertEquals(2, retrievedList.size());
        assertTrue(retrievedList.contains(dtoList.get(0)));
        assertTrue(retrievedList.contains(dtoList.get(1)));
    }

    @Test
    void testGetWithInvalidKey() {
        String uid = "user123";
        when(authUtil.getUID()).thenReturn(uid);

        TestDTO result = contextStorageService.get("nonExistentKey", TestDTO.class);

        assertNull(result);
    }

    @Test
    void testSetAndRemove() {
        String uid = "user123";
        when(authUtil.getUID()).thenReturn(uid);

        TestDTO dto = new TestDTO("toBeRemoved");
        contextStorageService.set("removableKey", dto);

        TestDTO beforeRemove = contextStorageService.get("removableKey", TestDTO.class);
        assertNotNull(beforeRemove);

        contextStorageService.remove("removableKey");
        TestDTO afterRemove = contextStorageService.get("removableKey", TestDTO.class);
        assertNull(afterRemove);
    }

    @Test
    void testClear() {
        String uid = "user123";
        when(authUtil.getUID()).thenReturn(uid);

        contextStorageService.set("key1", new TestDTO("value1"));
        contextStorageService.set("key2", new TestDTO("value2"));

        assertNotNull(contextStorageService.get("key1", TestDTO.class));
        assertNotNull(contextStorageService.get("key2", TestDTO.class));

        contextStorageService.clear();

        assertNull(contextStorageService.get("key1", TestDTO.class));
        assertNull(contextStorageService.get("key2", TestDTO.class));
    }

    static class TestDTO {
        private String value;

        public TestDTO(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            TestDTO testDTO = (TestDTO) obj;
            return value.equals(testDTO.value);
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }
    }
}