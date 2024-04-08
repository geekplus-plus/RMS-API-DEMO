package com.geekplus.demo.api.callback.dto;

import java.util.Objects;
import java.util.UUID;

import cn.hutool.core.util.StrUtil;


public class GoReturnMessage {

    /**
     * 已经在机器人身上的货箱归还到货位中
     */
    public static String generateGoReturnRequestMessage(String boxCode, Integer robotId, Integer destAreaId) {
        String message = "{\n"
                + "    \"id\": \"postman_001\",\n"
                + "    \"msgType\": \"RobotTaskRequestMsg\",\n"
                + "    \"request\": {\n"
                + "        \"header\": {\n"
                + "            \"channelId\": \"postman_001\",\n"
                + "            \"requestId\": \""+UUID.randomUUID().toString()+"\",\n"
                + "            \"clientCode\": \"geek\",\n"
                + "            \"warehouseCode\": \"geek\",\n"
                + "            \"userId\": \"admin\",\n"
                + "            \"userKey\": \"123456\",\n"
                + "            \"version\": \"3.3.0\",\n"
                + "            \"language\": \"en_us\"\n"
                + "        },\n"
                + "        \"body\": {\n"
                + "            \"taskType\": \"DELIVER_BOX\",\n"
                + "            \"instruction\": \"GO_RETURN\",\n"
                + "            \"boxCode\":\""+boxCode+"\",\n"
                + "            \"robotId\":"+robotId+",\n"
                + "            \"destAreaId\": "+destAreaId+"\n"
                + "        }\n"
                + "    }\n"
                + "}";
        return message;
    }

    /**
     * 货箱搬运到指定货位的消息, 库内从一个货位搬到另一个货位
     * @param boxCode
     * @param destLatticeCode
     * @return
     */
    public static String generateGoReturnMessage(String boxCode, String destLatticeCode, Integer stationId) {
        String message = "{\n"
                + "    \"id\": \"postman_001\",\n"
                + "    \"msgType\": \"RobotTaskRequestMsg\",\n"
                + "    \"request\": {\n"
                + "        \"header\": {\n"
                + "            \"channelId\": \"postman_001\",\n"
                + "            \"requestId\": \""+UUID.randomUUID().toString()+"\",\n"
                + "            \"clientCode\": \"geek\",\n"
                + "            \"warehouseCode\": \"geek\",\n"
                + "            \"userId\": \"admin\",\n"
                + "            \"userKey\": \"123456\",\n"
                + "            \"version\": \"3.3.0\",\n"
                + "            \"language\": \"en_us\"\n"
                + "        },\n"
                + "        \"body\": {\n"
                + "            \"taskType\": \"DELIVER_BOX\",\n"
                + "            \"instruction\": \"GO_RETURN\",\n"
                + "            \"boxCode\":\""+boxCode+"\",\n";
        if (Objects.nonNull(stationId)) {
            message += "            \"stationId\":"+stationId+",\n";
        }
        if (StrUtil.isNotBlank(destLatticeCode)) {
            message += "            \"latticeCode\":\""+destLatticeCode+"\",\n";
        }
        message += "            \"isContinue\": 0\n"
                + "        }\n"
                + "    }\n"
                + "}";
        return message;
    }

    /**
     *
     * @param jobId
     * @param boxCode
     * @param isContinue 是否结束任务， 0是1否
     * @return
     */
    public static String generateGoReturnMessage(String requestId, Long jobId, String boxCode, int isContinue, String placeLatticeCode) {
        String message = "{\n"
                + "    \"id\": \"postman_001\",\n"
                + "    \"msgType\": \"RobotTaskUpdateRequestMsg\",\n"
                + "    \"request\": {\n"
                + "        \"header\": {\n"
                + "            \"channelId\": \"postman_001\",\n"
                + "            \"requestId\": \"" + requestId + "\",\n"
                + "            \"clientCode\": \"geek\",\n"
                + "            \"warehouseCode\": \"geek\",\n"
                + "            \"userId\": \"admin\",\n"
                + "            \"userKey\": \"123456\",\n"
                + "            \"version\": \"3.3.0\",\n"
                + "            \"language\": \"en_us\"\n"
                + "        },\n"
                + "        \"body\": {\n"
                + "            \"taskId\": "+jobId+",\n"
                + "            \"taskType\": \"DELIVER_BOX\",\n"
                + "            \"instruction\": \"GO_RETURN\",\n";
                if (StrUtil.isNotBlank(boxCode)) {
                    message += "            \"boxCode\": \"" + boxCode + "\",\n";
                }
                if (StrUtil.isNotBlank(placeLatticeCode)) {
                    message += "            \"latticeCode\": \""+ placeLatticeCode + "\",\n";
                }
        message  +=   "            \"isContinue\": "+isContinue+"\n"
                 + "        }\n"
                 + "    }\n"
                 + "}";
        return message;
    }

    public static String generateGoReturnMessage(Long jobId, int isContinue) {
        String message = "{\n"
                + "    \"id\": \"postman_001\",\n"
                + "    \"msgType\": \"RobotTaskUpdateRequestMsg\",\n"
                + "    \"request\": {\n"
                + "        \"header\": {\n"
                + "            \"channelId\": \"postman_001\",\n"
                + "            \"requestId\": \"" + UUID.randomUUID().toString() + "\",\n"
                + "            \"clientCode\": \"geek\",\n"
                + "            \"warehouseCode\": \"geek\",\n"
                + "            \"userId\": \"admin\",\n"
                + "            \"userKey\": \"123456\",\n"
                + "            \"version\": \"3.3.0\",\n"
                + "            \"language\": \"en_us\"\n"
                + "        },\n"
                + "        \"body\": {\n"
                + "            \"taskId\": "+jobId+",\n"
                + "            \"taskType\": \"DELIVER_BOX\",\n"
                + "            \"instruction\": \"GO_RETURN\",\n";
        message  +=   "            \"isContinue\": "+isContinue+"\n"
                + "        }\n"
                + "    }\n"
                + "}";
        return message;
    }

    public static String generateGoReturnMessage(Long jobId, boolean destAsPlace) {
        String message = "{\n"
                + "    \"id\": \"postman_001\",\n"
                + "    \"msgType\": \"RobotTaskUpdateRequestMsg\",\n"
                + "    \"request\": {\n"
                + "        \"header\": {\n"
                + "            \"channelId\": \"postman_001\",\n"
                + "            \"requestId\": \"" + UUID.randomUUID().toString() + "\",\n"
                + "            \"clientCode\": \"geek\",\n"
                + "            \"warehouseCode\": \"geek\",\n"
                + "            \"userId\": \"admin\",\n"
                + "            \"userKey\": \"123456\",\n"
                + "            \"version\": \"3.3.0\",\n"
                + "            \"language\": \"en_us\"\n"
                + "        },\n"
                + "        \"body\": {\n"
                + "            \"taskId\": "+jobId+",\n"
                + "            \"destAsPlace\":" +destAsPlace+",\n"
                + "            \"taskType\": \"DELIVER_BOX\",\n"
                + "            \"instruction\": \"GO_RETURN\",\n";
        message  +=   "            \"isContinue\": "+0+"\n"
                + "        }\n"
                + "    }\n"
                + "}";
        return message;
    }

    public static String generateGoReturnUpdateMessage(Long jobId, String boxCode) {
        String message = "{\n"
                + "    \"id\": \"postman_001\",\n"
                + "    \"msgType\": \"RobotTaskUpdateRequestMsg\",\n"
                + "    \"request\": {\n"
                + "        \"header\": {\n"
                + "            \"channelId\": \"postman_001\",\n"
                + "            \"requestId\": \"" + UUID.randomUUID().toString() + "\",\n"
                + "            \"clientCode\": \"geek\",\n"
                + "            \"warehouseCode\": \"geek\",\n"
                + "            \"userId\": \"admin\",\n"
                + "            \"userKey\": \"123456\",\n"
                + "            \"version\": \"3.3.0\",\n"
                + "            \"language\": \"en_us\"\n"
                + "        },\n"
                + "        \"body\": {\n"
                + "            \"taskId\": " + jobId + ",\n"
                + "            \"taskType\": \"DELIVER_BOX\",\n"
                + "            \"instruction\": \"GO_RETURN\",\n"
                + "            \"boxCode\": \"" + boxCode + "\",\n"
                + "            \"isContinue\": 0\n"
                + "        }\n"
                + "    }\n"
                + "}";
        return message;
    }

    /**
     * 空箱入场最后一步的GO_RETURN消息
     * @param jobId
     * @param destLatticeCode
     * @return
     */
    public static String generateUpdateMessage(Long jobId, String destLatticeCode, Integer destAreaId) {
        String message = "{\n"
                + "    \"id\": \"postman_001\",\n"
                + "    \"msgType\": \"RobotTaskUpdateRequestMsg\",\n"
                + "    \"request\": {\n"
                + "        \"header\": {\n"
                + "            \"channelId\": \"postman_001\",\n"
                + "            \"requestId\": \"" + UUID.randomUUID().toString() + "\",\n"
                + "            \"clientCode\": \"geek\",\n"
                + "            \"warehouseCode\": \"geek\",\n"
                + "            \"userId\": \"admin\",\n"
                + "            \"userKey\": \"123456\",\n"
                + "            \"version\": \"3.3.0\",\n"
                + "            \"language\": \"en_us\"\n"
                + "        },\n"
                + "        \"body\": {\n"
                + "            \"taskId\": " + jobId + ",\n"
                + "            \"taskType\": \"DELIVER_BOX\",\n"
                + "            \"destAsPlace\": \"true\",\n"
                + "            \"instruction\": \"GO_RETURN\",\n";
        if (StrUtil.isNotBlank(destLatticeCode)) {
            message += "            \"latticeCode\":\"" + destLatticeCode + "\",\n";
        }

        if (Objects.nonNull(destAreaId)) {
            message += "            \"destAreaId\":\"" + destAreaId + "\",\n";
        }
                message += "            \"isContinue\": 0\n"
                + "        }\n"
                + "    }\n"
                + "}";
        return message;
    }

    public static String generateAddBoxByModelId(String latticeCode, String boxModelId) {
        String message = "{\n"
                + "    \"id\": \"postman_001\",\n"
                + "    \"msgType\": \"WarehouseInstructionRequestMsg\",\n"
                + "    \"request\": {\n"
                + "        \"header\": {\n"
                + "            \"channelId\": \"postman_001\",\n"
                + "            \"requestId\": \"" + UUID.randomUUID().toString() + "\",\n"
                + "            \"clientCode\": \"geek\",\n"
                + "            \"warehouseCode\": \"geek\",\n"
                + "            \"userId\": \"admin\",\n"
                + "            \"userKey\": \"123456\",\n"
                + "            \"version\": \"3.3.0\",\n"
                + "            \"language\": \"en_us\"\n"
                + "        },\n"
                + "        \"body\": {\n"
                + "            \"instruction\": \"ADD_BOX\",\n"
                + "            \"boxCode\": \"" + "TB" + latticeCode + "\",\n";
            if (StrUtil.isNotEmpty(boxModelId)) {
                message+= "      \"boxModelId\": \"" + boxModelId + "\",\n";
            }
        message+= "            \"latticeCode\": \"" + latticeCode + "\",\n"
                + "            \"placeLatticeCode\": \"" + latticeCode + "\"\n"
                + "        }\n"
                + "    }\n"
                + "}";
        return message;
    }

    public static String generateAddBox(String boxCode, String latticeCode) {
        String message = "{\n"
                + "    \"id\": \"postman_001\",\n"
                + "    \"msgType\": \"WarehouseInstructionRequestMsg\",\n"
                + "    \"request\": {\n"
                + "        \"header\": {\n"
                + "            \"channelId\": \"postman_001\",\n"
                + "            \"requestId\": \"" + UUID.randomUUID().toString() + "\",\n"
                + "            \"clientCode\": \"geek\",\n"
                + "            \"warehouseCode\": \"geek\",\n"
                + "            \"userId\": \"admin\",\n"
                + "            \"userKey\": \"123456\",\n"
                + "            \"version\": \"3.3.0\",\n"
                + "            \"language\": \"en_us\"\n"
                + "        },\n"
                + "        \"body\": {\n"
                + "            \"instruction\": \"ADD_BOX\",\n"
                + "            \"boxCode\": \"" + boxCode + "\",\n"
                + "            \"latticeCode\": \"" + latticeCode + "\",\n"
                + "            \"placeLatticeCode\": \"" + latticeCode + "\"\n"
                + "        }\n"
                + "    }\n"
                + "}";
        return message;
    }

    public static String generateAddShelf(String cellCode, String shelfClassCode, String shelfAngle) {
        String message = "{\n"
                + "    \"id\": \"postman_001\",\n"
                + "    \"msgType\": \"WarehouseInstructionRequestMsg\",\n"
                + "    \"request\": {\n"
                + "        \"header\": {\n"
                + "            \"channelId\": \"postman_001\",\n"
                + "            \"requestId\": \"" + UUID.randomUUID().toString() + "\",\n"
                + "            \"clientCode\": \"geek\",\n"
                + "            \"warehouseCode\": \"geek\",\n"
                + "            \"userId\": \"admin\",\n"
                + "            \"userKey\": \"123456\",\n"
                + "            \"version\": \"3.3.0\",\n"
                + "            \"language\": \"en_us\"\n"
                + "        },\n"
                + "        \"body\": {\n"
                + "            \"instruction\": \"ADD_SHELF\",\n"
                + "            \"shelfCode\": \"" + shelfClassCode + cellCode + "\",\n"
                + "            \"locationCellCode\": \"" + cellCode + "\",\n"
                + "            \"placementCellCode\": \"" + cellCode + "\",\n"
                + "            \"shelfAngle\": " + Integer.valueOf(shelfAngle) + ",\n"
                + "            \"shelfClassCode\": \"" + shelfClassCode + "\"\n"
                + "        }\n"
                + "    }\n"
                + "}";
        return message;
    }

    public static String generateEmptyBoxToStationMessage(Integer stationId) {
        String message = "{\n"
                + "    \"id\": \"postman_001\",\n"
                + "    \"msgType\": \"RobotTaskRequestMsg\",\n"
                + "    \"request\": {\n"
                + "        \"header\": {\n"
                + "            \"channelId\": \"postman_001\",\n"
                + "            \"requestId\": \""+UUID.randomUUID().toString()+"\",\n"
                + "            \"clientCode\": \"geek\",\n"
                + "            \"warehouseCode\": \"geek\",\n"
                + "            \"userId\": \"geek\",\n"
                + "            \"userKey\": \"geek\",\n"
                + "            \"version\": \"3.3.0\",\n"
                + "            \"language\": \"en_us\"\n"
                + "        },\n"
                + "        \"body\": {\n"
                + "            \"taskType\": \"DELIVER_BOX\",\n"
                + "            \"instruction\": \"GO_RETURN\",\n"
                + "            \"boxCode\": \"*\",\n"
                + "            \"stationId\": "+stationId+"        }\n"
                + "    }\n"
                + "}";
        return message;
    }

    public static String generateToStationRackMessage(String boxCode, Integer stationId) {
        String message = "{\n"
                + "    \"id\": \"postman_001\",\n"
                + "    \"msgType\": \"RobotTaskRequestMsg\",\n"
                + "    \"request\": {\n"
                + "        \"header\": {\n"
                + "            \"channelId\": \"postman_001\",\n"
                + "            \"requestId\": \""+UUID.randomUUID().toString()+"\",\n"
                + "            \"clientCode\": \"geek\",\n"
                + "            \"warehouseCode\": \"geek\",\n"
                + "            \"userId\": \"geek\",\n"
                + "            \"userKey\": \"geek\",\n"
                + "            \"version\": \"3.3.0\",\n"
                + "            \"language\": \"en_us\"\n"
                + "        },\n"
                + "        \"body\": {\n"
                + "            \"taskType\": \"DELIVER_BOX\",\n"
                + "            \"instruction\": \"GO_RETURN\",\n"
                + "            \"boxCode\": \"" + boxCode + "\",\n"
                + "            \"stationId\": "+stationId+"        }\n"
                + "    }\n"
                + "}";
        return message;
    }

}
