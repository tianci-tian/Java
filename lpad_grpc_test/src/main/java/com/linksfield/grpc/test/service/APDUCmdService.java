package com.linksfield.grpc.test.service;

import com.linksfield.grpc.test.pojo.ApduStr;
import com.linksfield.grpc.test.smartcard.CardReader;
import com.linksfield.grpc.test.smartcard.HexStr;

import javax.smartcardio.*;
import java.util.ArrayList;
import java.util.List;

public class APDUCmdService {
    ApduStr g_apduStr = new ApduStr();
    CardReader g_cardReader = new CardReader();
    List<CardTerminal> g_cardTerminalList = new ArrayList<CardTerminal>();
    Card g_card;
    byte[] g_apducmd;
    CommandAPDU g_commandAPDU;
    ResponseAPDU g_responseAPDU;
    byte[] g_apduRespData;

    public void reset() throws CardException {
        g_cardTerminalList =  g_cardReader.getCardTerminals();
        g_card = g_cardReader.ReaderConnect(g_cardTerminalList.get(0));
    }

    public ApduStr DealAPDUCmdService(String apduCmdStr){
        String apduRespData_str = "";
        String apduRespSw_str = "";
        String apduRes_str = "";

        g_apduStr.setApduCmd(apduCmdStr);

        g_apducmd = HexStr.hexToBuffer(apduCmdStr);
        g_commandAPDU = new CommandAPDU(g_apducmd);
        g_responseAPDU = g_cardReader.getOneCmdResponse(g_card, g_commandAPDU);
        g_apduRespData = g_responseAPDU.getData();
        apduRespData_str = HexStr.bufferToHex(g_apduRespData);
        apduRespSw_str = Integer.toHexString(g_responseAPDU.getSW());
        apduRes_str = apduRespData_str + apduRespSw_str;
        g_apduStr.setApduRespData(apduRespData_str);
        g_apduStr.setApduRespSw(apduRespSw_str);
        g_apduStr.setApduResp(apduRes_str);

        System.out.println("apducmd:"+g_apduStr.getApduCmd());
        System.out.println("apduResp:"+g_apduStr.getApduResp());
        System.out.println("apduRespSw:"+g_apduStr.getApduRespSw());
        System.out.println("apduRespData:"+g_apduStr.getApduRespData());

        return g_apduStr;
    }

    public void disConnectCard() throws CardException {
        g_cardReader.ReaderDisConnect(g_card);
    }

    public void ApduStrPrint(ApduStr apduStr){
        System.out.println("apducmd:"+apduStr.getApduCmd());
        System.out.println("apduResp:"+apduStr.getApduResp());
        System.out.println("apduRespSw:"+apduStr.getApduRespSw());
        System.out.println("apduRespData:"+apduStr.getApduRespData());
    }
}
