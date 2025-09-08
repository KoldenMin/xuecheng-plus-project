package com.xuecheng.base.exception;

import javax.validation.groups.Default;

/**
 * @author jiaohan
 * @description des
 * 2025-08-12
 */
public class ValidationGroups {

    public interface Insert extends Default {
    }

    public interface Update extends Default {
    }

    public interface Delete {
    }
}
