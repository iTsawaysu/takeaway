package com.sun.takeaway.common;

import lombok.Data;
import java.io.Serializable;

/**
 * @author sun
 */
@Data
public class DeleteRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
}
