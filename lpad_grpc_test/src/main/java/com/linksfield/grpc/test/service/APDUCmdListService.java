package com.linksfield.grpc.test.service;

import com.linksfield.grpc.test.pojo.ApduStr;
import com.linksfield.grpc.test.smartcard.CardReader;
import com.linksfield.grpc.test.smartcard.HexStr;

import javax.smartcardio.*;
import java.util.ArrayList;
import java.util.List;

public class APDUCmdListService {
    List<ApduStr> g_apduListStr = new ArrayList<ApduStr>();
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

    public List<ApduStr> DealAPDUCmdlListService(List<String> apduCmdListStr){
        String apduRespData_str = "";
        String apduRespSw_str = "";
        String apduRes_str = "";
        String apduCmdStr = "";

        for (int i = 0; i < apduCmdListStr.size() ; i++) {
            apduCmdStr = apduCmdListStr.get(i);
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

            g_apduListStr.add(g_apduStr);


        }

        return g_apduListStr;
    }

    public void disConnectCard() throws CardException {
        g_cardReader.ReaderDisConnect(g_card);
    }

    public void ApduListStrPrint(List<ApduStr> apduListStr){
        for (int i = 0; i < apduListStr.size() ; i++) {
            System.out.println("apducmd:"+apduListStr.get(i).getApduCmd());
            System.out.println("apduResp:"+apduListStr.get(i).getApduResp());
            System.out.println("apduRespSw:"+apduListStr.get(i).getApduRespSw());
            System.out.println("apduRespData:"+apduListStr.get(i).getApduRespData());
        }
    }

}
