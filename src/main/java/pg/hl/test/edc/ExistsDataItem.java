package pg.hl.test.edc;

import pg.hl.DevException;
import pg.hl.test.TestUtils;
import pg.hl.test.hb.HibernateTestItem;

import java.util.*;
import java.util.stream.Collectors;

public abstract class ExistsDataItem<TypeSource, TypeId, TypeCode, TypeInitArgument> {
    private static final Integer MAX_RANDOM_COUNT = 100;
    private static final Random RANDOM = new Random();
    private final Map<Object, Set<Object>> usedRandomValuesRepository = new HashMap<>();
    private final Map<TypeCode, TypeId> items = new HashMap<>();
    private final TypeCode[] codes;

    public ExistsDataItem(HibernateTestItem<?, ?, ?> defaultTestItem, int size, Class<TypeSource> typeSourceClazz, Class<TypeCode> typeCodeClazz) {
        var sourceItems = getSource(defaultTestItem);
        if (sourceItems.size() == 0) {
            var argument = createInitArgument();
            sourceItems = TestUtils.EASY_RANDOM.objects(typeSourceClazz, size)
                    .peek(p -> modifyNew(p, argument))
                    .collect(Collectors.toList());
            defaultTestItem.save(sourceItems);
        }

        for (var item : sourceItems) {
            items.put(getCode(item), getId(item));
        }

        codes = items.keySet().stream().map(typeCodeClazz::cast).toArray(this::createCodesArray);
        if (codes.length <= 0) {
            throw new DevException("Статусы отсутствуют в БД!");
        }
    }

    protected abstract TypeCode[] createCodesArray(int size);

    protected abstract Collection<TypeSource> getSource(HibernateTestItem<?, ?, ?> defaultTestItem);

    protected abstract TypeId getId(TypeSource source);

    protected abstract TypeCode getCode(TypeSource source);

    protected abstract TypeInitArgument createInitArgument();

    protected abstract void modifyNew(TypeSource source, TypeInitArgument initArgument);

    public TypeCode getRandomCode(Object uniqueKey) {
        Set<Object> usedRandomValues = usedRandomValuesRepository.computeIfAbsent(uniqueKey, k -> new HashSet<>());

        for (int i = 0; i < MAX_RANDOM_COUNT; i++) {
            TypeCode result = getRandomCode();
            if (usedRandomValues.add(result)) {
                return result;
            }
        }

        throw new DevException("Не удалось получить случайное уникальное значение");
    }

    public TypeCode getRandomCode() {
        return codes[RANDOM.nextInt(codes.length - 1)];
    }

    public TypeId resolve(TypeCode code) {
        return items.get(code);
    }
}
