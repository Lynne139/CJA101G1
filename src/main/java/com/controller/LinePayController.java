package com.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.shopOrd.model.ShopOrdService;
import com.utils.ApiResponse;
import com.utils.HmacUtil;

import jakarta.servlet.http.HttpServletResponse;

@RestController
public class LinePayController {

    @Autowired
    private ShopOrdService shopOrdService;

    @PostMapping("/api/linepay/{isCamp}")
    public ApiResponse<String> doLinePay(@PathVariable Boolean isCamp, @RequestBody String jsonBody,
            HttpServletResponse response) throws IOException, JSONException {
        final String channelId = "1656895462";
        final String CHANNEL_SECRET = "fd01e635b9ea97323acbe8d5c6b2fb71";
        final String API_URL = "https://sandbox-api-pay.line.me/v3/payments/request";

        // 解析原本的 JSON
        JSONObject body = new JSONObject(jsonBody);
        JSONObject linepayBody = body.optJSONObject("linepayBody");
        JSONObject linepayOrder = body.optJSONObject("linepayOrder");
        System.out.println("jsonBody:" + body);
        System.out.println("linepayBody:" + linepayBody);
        System.out.println("linepayOrder:" + linepayOrder);

        // 產生 HMAC 簽章
        String nonce = UUID.randomUUID().toString();
        String uri = "/v3/payments/request";
        String signatureRaw = CHANNEL_SECRET + uri + linepayBody.toString() + nonce;
        String signature = HmacUtil.hmacSHA256Base64(CHANNEL_SECRET, signatureRaw);

        // 發送 HTTP POST 請求到 LINE Pay
        HttpURLConnection conn = (HttpURLConnection) new URL(API_URL).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("X-LINE-ChannelId", channelId);
        conn.setRequestProperty("X-LINE-Authorization", signature);
        conn.setRequestProperty("X-LINE-Authorization-Nonce", nonce);
        conn.setDoOutput(true);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(linepayBody.toString().getBytes("UTF-8"));
        }
        // 取得回應
        StringBuilder responseLinePay = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String respLine;
            while ((respLine = br.readLine()) != null)
                responseLinePay.append(respLine);
        }
        JSONObject resJson = new JSONObject(responseLinePay.toString());
        System.out.println("resJson:" + resJson);
        JSONObject info = resJson.optJSONObject("info");
        String returnCode = resJson.getString("returnCode");

        if (returnCode.equals("0000")) {
            // 付款網址請求成功，將訂單資料(未付款)塞入DB
            if (isCamp) {
                System.out.println("Camp");
                // campsiteOrdSvc.createOneCampOrderJson(linepayOrder);
            } else {
                System.out.println("Shop");
                // 將 linepayOrder 轉換為 Map 並傳給 ShopOrdService
                Map<String, Object> orderMap = linepayOrder.toMap();
                shopOrdService.createShopOrderFromLinePay(orderMap);
            }

            System.out.println("aaaaaa");
            String paymentUrl = (info != null && info.has("paymentUrl"))
                    ? info.getJSONObject("paymentUrl").getString("web")
                    : null;

            // 回傳 paymentUrl 給前端
            return new ApiResponse<>("success", paymentUrl, "查詢成功");
        } else {
            // 交易失敗
            return new ApiResponse<>("fail", "fail", "查詢失敗");
        }

    }

    // 確認LINEPAY付款狀態
    @GetMapping("api/confirmpayment/{orderId}/{isCamp}")
    public void checkLinePayStatus(@PathVariable String orderId, @PathVariable Boolean isCamp,
            HttpServletResponse responseServlet)
            throws IOException, URISyntaxException, JSONException {
        final String channelId = "1656895462";
        final String CHANNEL_SECRET = "fd01e635b9ea97323acbe8d5c6b2fb71";
        final String API_URL = "https://sandbox-api-pay.line.me/v2/payments/orders/" + orderId + "/check";

        // 發送 HTTP POST 請求到 LINE Pay
        HttpURLConnection conn = (HttpURLConnection) new URL(API_URL).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("X-LINE-ChannelId", channelId);
        conn.setRequestProperty("X-LINE-ChannelSecret", CHANNEL_SECRET);

        // 發送request
        int responseCode = conn.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // 解析 JSON 字串
        JSONObject json = new JSONObject(response.toString());
        String returnCode = json.getString("returnCode");
        String returnMessage = json.getString("returnMessage");
        System.out.println("LINEPAY交易(" + orderId + "): " + returnCode + "||" + returnMessage);
        if (returnCode.equals("0000")) {
            // 交易成功，將訂單狀態改成已付款塞入DB
            if (isCamp) {
                System.out.println("Camp");
                // campsiteOrdSvc.updatePaymentStatus(orderId, (byte)1);
                responseServlet.sendRedirect("http://127.0.0.1:5501/linepay-success.html?orderId=" + orderId + "&isCamp=" + isCamp);
            } else {
                System.out.println("Shop");
                // 這裡要改成商品的成功頁面
                responseServlet.sendRedirect("http://localhost:8080/front-end/shop?payment=success&orderId=" + orderId);
            }

        } else {
            // 交易失敗
            responseServlet.sendRedirect("http://localhost:8080/front-end/shop?payment=failed&orderId=" + orderId);
        }
    }
} 