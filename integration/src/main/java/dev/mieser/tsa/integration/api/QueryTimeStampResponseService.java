package dev.mieser.tsa.integration.api;

import dev.mieser.tsa.domain.TimestampResponseData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QueryTimeStampResponseService {

    Page<TimestampResponseData> findAll(Pageable pageable);

}