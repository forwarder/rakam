/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.rakam.analysis;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.rakam.realtime.AggregationType;
import org.rakam.report.QueryResult;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static com.google.common.base.Preconditions.checkNotNull;


public interface EventExplorer {
    CompletableFuture<QueryResult> analyze(String project, List<String> collections, Measure measureType, Reference grouping, Reference segment, String filterExpression, LocalDate startDate, LocalDate endDate);
    CompletableFuture<QueryResult> getEventStatistics(String project, Optional<Set<String>> collections, Optional<String> dimension, LocalDate startDate, LocalDate endDate);
    List<String> getExtraDimensions(String project);

    enum TimestampTransformation {
        HOUR_OF_DAY("Hour of day"), DAY_OF_MONTH("Day of month"),
        WEEK_OF_YEAR("Week of year"), MONTH_OF_YEAR("Month of year"),
        QUARTER_OF_YEAR("Quarter of year"), DAY_PART("Day part"),
        DAY_OF_WEEK("Day of week"), HOUR("Hour"),
        DAY("Day"), WEEK("Week"),
        MONTH("Month"), YEAR("Year");

        private final String prettyName;

        TimestampTransformation(String name) {
            this.prettyName = name;
        }

        @JsonCreator
        public static TimestampTransformation fromString(String key) {
            return key == null ? null : valueOf(key.toUpperCase());
        }

        public String getPrettyName() {
            return prettyName;
        }

        public static Optional<TimestampTransformation> fromPrettyName(String name) {
            for (TimestampTransformation transformation : values()) {
                if(transformation.getPrettyName().equals(name)) {
                    return Optional.of(transformation);
                }
            }
            return Optional.empty();
        }
    }

    class Measure {
        public final String column;
        public final AggregationType aggregation;

        @JsonCreator
        public Measure(@JsonProperty("column") String column,
                       @JsonProperty("aggregation") AggregationType aggregation) {
            if(column == null && aggregation != AggregationType.COUNT) {
                throw new IllegalArgumentException("measure column is required if aggregation is not COUNT");
            }
            this.column = column;
            this.aggregation = Objects.requireNonNull(aggregation, "aggregation is null");
        }
    }

    class Reference {
        public final ReferenceType type;
        public final String value;

        @JsonCreator
        public Reference(@JsonProperty("type") ReferenceType type,
                         @JsonProperty("value") String value) {
            this.type = checkNotNull(type, "type is null");
            this.value = checkNotNull(value, "value is null");
        }
    }

    enum ReferenceType {
        COLUMN, REFERENCE;

        @JsonCreator
        public static ReferenceType get(String name) {
            return valueOf(name.toUpperCase());
        }

        @JsonProperty
        public String value() {
            return name();
        }
    }
}
