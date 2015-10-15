package org.rakam;

import com.google.common.base.Throwables;
import com.google.inject.name.Named;
import com.impossibl.postgres.api.jdbc.PGConnection;
import org.rakam.analysis.JDBCPoolDataSource;
import org.rakam.plugin.CollectionStreamQuery;
import org.rakam.plugin.EventStream;
import org.rakam.plugin.StreamResponse;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.List;

public class PostgresqlEventStream implements EventStream {
    private final JDBCPoolDataSource dataSource;

    @Inject
    public PostgresqlEventStream(@Named("async-postgresql") JDBCPoolDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public EventStreamer subscribe(String project, List<CollectionStreamQuery> collections, List<String> columns, StreamResponse response) {
        try(PGConnection conn = (PGConnection) dataSource.openConnection()) {
            return new PostgresqlEventStreamer(conn, project, collections, response);
        } catch (SQLException e) {
            throw Throwables.propagate(e);
        }
    }



}
