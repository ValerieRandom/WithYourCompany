# **Log Error Debug Tool 使用說明**

## **1. 概要**
這是一個 **Log Error Debug Tool**，用來自動掃描 log 文件中的錯誤，並將符合條件的上下文內容儲存為 **txt 檔案**。此外，該工具還會根據本機 IP 動態命名輸出檔案，並將結果檔自動上傳至 **FTP 伺服器**。

---

## **2. 使用方法**

### **2.1 設定 config.properties**
此專案包含 **`config.properties`**，使用者可透過此設定檔調整要抓取的錯誤模式、log 文件路徑，以及 FTP 上傳資訊。

- **log.path**：  
  設定要掃描的 log 文件資料夾，該小工具將會遍歷此資料夾內所有 .log 或 .txt 檔，並在最後產出一份掃描過後之 .txt 檔例如：
  ```properties
  log.path=C:\\Users\\TEMP\\logs
  ```

- **錯誤模式設計（正則表達式）**：  
  使用 `err1`、`err2` 等命名規則來定義不同的錯誤匹配模式，若需新增請以 `err` 開頭。例如：
  ```properties
  err1=.*?(Connection closed|Connection failed|Connection reset).*?
  err6=.*?(New Error Pattern Example).*?
  ```

  **正則表達式語法說明：**
   - `.*?`：匹配任意字元 0 次或多次。
   - `|`：表示「或」邏輯。例如：`Connection closed|Connection failed`。
   - `\d+`：匹配一個或多個數字。

- **上下文行數設定**：  
  透過 `lines.before` 和 `lines.after` 調整匹配行的上下文行數：
  ```properties
  lines.before=10
  lines.after=10
  ```
   - **預設值**：上下各 10 行。若未在 config.properties 中設置，系統會自動使用程式碼中之預設值上下 10 行。

- **本地輸出檔案路徑**：  
  在上傳前，檔案會先存放於指定的本地路徑，此參數預設為未開啟：
  ```properties
  output.path=C:\\Users\\TEMP\\output
  ```

- **FTP 上傳設定**：  
  於 `config.properties` 中設定 FTP 伺服器資訊：
  ```properties
  ftp.host=ftp.example.com
  ftp.user=ftp_user
  ftp.pass=ftp_password
  ```

---

### **2.2 執行程式**
1. 在命令列中輸入以下指令來執行此工具：
   ```bash
   java -jar LogErrorTool.jar
   ```

2. 系統會自動掃描指定的 **資料夾文件**，匹配所有符合條件的錯誤。

3. 若找到匹配錯誤，工具會：
   1. 將錯誤行及其上下文行數儲存成 .txt 檔並**自動上傳**其至指定 FTP 伺服器。
---

### **3. 輸出結果**

#### **3.1 檔案命名規則**
輸出檔案的命名會依據本機裝置的 IP 位址自動命名：
```
ERRORScanned_{ip}.txt
```
例如：若 IP 為 `192.168.1.5`，則檔案名稱為：
```
ERRORScanned_192_168_1_5.txt
```

#### **3.2 檔案內容格式**
以下是輸出檔案的範例內容：
```
==== 上下文 ====
匹配到的正則模式: err1
>>> 5: 2024-05-30 02:02:37 [ERROR] FTPUtil Connection closed without indication. <<<
    6: 其他不相關的 log 行
================
```
- **`>>> <<<`**：用於標示匹配行，方便快速定位。

---

## **4. 注意事項**
1. 請確認 **config.properties** 中的 FTP 伺服器資訊正確，避免上傳失敗。
2. 執行程式前，請確保 FTP 伺服器與需要被掃描之 log 資料夾皆已存在。
3. 若出現上傳失敗，請檢查 FTP 伺服器的連線狀態或帳號密碼。

---

## **5. 常見問題**

### **Q1：為什麼檔案無法上傳？**
1. 請確認 **FTP 伺服器**是否正常運行。
2. 檢查 **帳號密碼**是否正確，並確認伺服器允許上傳操作。

### **Q2：如何新增自訂的錯誤模式？**
在 **`config.properties`** 中以 `err6`、`err7` 依序增加新模式，例如：
```properties
err6=.*?(Custom Error Pattern Example).*?
err7=.*?(Another Error Example).*?
```

### **Q3：我該如何驗證上傳成功？**
執行程式後，若上傳成功，系統會於終端機顯示：
```
FTP 上傳成功: ERRORScanned_192_168_1_5.txt
```
若上傳失敗，則會顯示錯誤訊息，請檢查 FTP 設定。

---