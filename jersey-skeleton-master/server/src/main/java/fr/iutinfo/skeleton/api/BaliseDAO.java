package fr.iutinfo.skeleton.api;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import org.skife.jdbi.v2.tweak.BeanMapperFactory;

public interface BaliseDAO {
    @SqlUpdate("create table balise (id integer primary key autoincrement, longitude double, latitude double, parcours int, FOREIGN KEY (parcours) REFERENCES parcours(id))")
    void createBaliseTable();

    @SqlUpdate("insert into balise (longitude,latitude,parcours) values (:longitude, :latitude, :parcours)")
    @GetGeneratedKeys
    int insert(@BindBean() Balise balise);

    @SqlQuery("select * from balise where name = :name")
    @RegisterMapperFactory(BeanMapperFactory.class)
    Balise findByName(@Bind("name") String name);

    @SqlQuery("select * from balise where search like :name")
    @RegisterMapperFactory(BeanMapperFactory.class)
    List<Balise> search(@Bind("name") String name);

    @SqlUpdate("drop table if exists balise")
    void dropBaliseTable();

    @SqlUpdate("delete from balise where id = :id")
    void delete(@Bind("id") int id);

    @SqlQuery("select * from balise order by id")
    @RegisterMapperFactory(BeanMapperFactory.class)
    List<Balise> all();

    @SqlQuery("select * from balise where parcours = :parcours")
	@RegisterMapperFactory(BeanMapperFactory.class)
	List<Balise> allFromParcours(@Bind("parcours") int parcours);

    @SqlQuery("select * from balise where id = :id")
    @RegisterMapperFactory(BeanMapperFactory.class)
    Balise findById(@Bind("id") int id);

    @SqlUpdate("update balise SET longitude = :longitude , latitude = :latitude  where id = :id")
    void updateBalise(@Bind("longitude")double longitude, @Bind("latitude") double latitude, @Bind("id") int id);
    
    void close();
}
