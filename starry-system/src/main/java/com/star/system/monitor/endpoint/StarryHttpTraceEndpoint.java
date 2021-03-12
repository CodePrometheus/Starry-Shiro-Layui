package com.star.system.monitor.endpoint;

import com.star.common.annotation.StarryEndPoint;
import org.springframework.boot.actuate.trace.http.HttpTrace;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;

import java.util.List;

/**
 * @Author: zzStar
 * @Date: 03-08-2021 13:45
 */
@StarryEndPoint
public class StarryHttpTraceEndpoint {

    private final HttpTraceRepository repository;

    public StarryHttpTraceEndpoint(HttpTraceRepository repository) {
        this.repository = repository;
    }

    public StarryHttpTraceDescriptor traces() {
        return new StarryHttpTraceDescriptor(repository.findAll());
    }

    public static final class StarryHttpTraceDescriptor {

        private final List<HttpTrace> traces;

        private StarryHttpTraceDescriptor(List<HttpTrace> traces) {
            this.traces = traces;
        }

        public List<HttpTrace> getTraces() {
            return traces;
        }
    }
}
