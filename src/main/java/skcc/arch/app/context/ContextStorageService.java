package skcc.arch.app.context;

public interface ContextStorageService {
    
    /**
     * 데이터를 저장합니다. uid에 대해 key와 value를 저장하며, key가 존재할 경우 덮어씁니다.
     *
     * @param key  저장할 데이터의 키
     * @param value 저장할 데이터 값 (Object)
     */
    void set(String key, Object value);

    /**
     * 데이터를 가져옵니다.
     *
     * @param key  찾고자 하는 데이터의 키
     * @param type 반환할 데이터 타입
     * @param <T>  반환 데이터 타입
     * @return key로 매칭된 값, 없을 경우 null
     */
    <T> T get(String key, Class<T> type);

    /**
     * 데이터를 삭제합니다.
     *
     * @param key 삭제할 데이터의 키
     */
    void remove(String key);

    /**
     * uid와 연관된 모든 데이터를 삭제합니다.
     *
     */
    void clear();
}