package com.linksfield.grpc.test.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApduStr {
    String apduCmd;              //APDU cmd

    String apduResp;             //实际返回数据 data+sw
    String apduRespSw;           //sw
    String apduRespData;         //data
}
