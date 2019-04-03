/***
 * @pName management
 * @name VersionResp
 * @user DF
 * @date 2018/9/5
 * @desc
 */
package com.management.admin.entity.resp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VersionResp {
    private Integer update;
    private String version;
}
