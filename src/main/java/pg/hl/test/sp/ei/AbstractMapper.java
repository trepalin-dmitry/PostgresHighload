package pg.hl.test.sp.ei;

import lombok.AccessLevel;
import lombok.Getter;
import pg.hl.dto.AbstractDataObject;
import pg.hl.dto.AbstractDataPackage;
import pg.hl.test.EntityType;
import pg.hl.test.IdentityStrategy;
import pg.hl.test.edc.ExistsDataController;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public abstract class AbstractMapper<TypePackage extends AbstractDataPackage<TypeDealSource>, TypeDealSource extends AbstractDataObject, TypeDealTarget> {
    @Getter(AccessLevel.PROTECTED)
    private final ExistsDataController existsDataController;

    protected AbstractMapper(IdentityStrategy identityStrategy, EntityType entityType) throws PropertyVetoException, SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        this.existsDataController = ExistsDataController.getOrCreate(identityStrategy, entityType);
    }

    public Collection<TypeDealTarget> parse(TypePackage exchangeDealsPackage) {
        var deals = new ArrayList<TypeDealTarget>();

        for (TypeDealSource dealSource : exchangeDealsPackage.getObjects()) {
            deals.add(parse(dealSource));
        }

        return deals;
    }

    protected abstract TypeDealTarget parse(TypeDealSource source);
}
