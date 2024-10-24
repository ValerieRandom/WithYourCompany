
# **ValerieTestAPIV2 操作手冊**

## **1. 切換至專案目錄並檢查檔案**

### **1.1 切換至專案目錄**
請使用以下指令切換到專案所在的目錄：

```bash
cd /PROG/ValerieTestAPIV2
```

### **1.2 檢查專案目錄的內容**
使用以下指令查看目錄內的檔案和資料夾：

```bash
ls
```

#### **目錄內容：**
```
APIF2.sh  APIFConfig  APIFV2.err  APIFV2.jar  APIFV2.jsvcout  lib
```

---

## **2. 目錄與檔案詳解**

### **2.1 APIF2.sh**
`APIF2.sh` 是專案的執行腳本，用於啟動和停止服務。

- **啟動服務：**
   ```bash
   ./APIF2.sh start
   ```

- **停止服務：**
   ```bash
   ./APIF2.sh stop
   ```

> **說明：**  
  此執行檔與 `APIF.sh` 內的執行內容相同。命名為 `APIF2.sh` 的目的是為了確保在測試環節中能夠完全區分，不會與其他執行環節混淆。

---

### **2.2 APIFConfig**
`APIFConfig` 是一個目錄，裡面包含了所有專案的配置檔案，如以下截圖所示：

- **`APIF-beans-config.xml`**：  
  這是 Spring 的核心配置檔，**會初始化** `business.conf`、`communication.conf` 和 `core.conf`，是將所有配置檔案整合在一起的重要文件。

- **`spring-beans.xsd`**：  
  本地 XSD 檔案，用於取代 URL 加載方式，以解決 **JDK 6** 無法跳轉至外部連結的問題。  
  > **重要提醒：**  
  **`APIF-beans-config.xml` 與 `spring-beans.xsd` 必須配合使用，缺一不可**，否則將無法正常解析 `APIF-beans-config.xml`，導致系統啟動失敗。

- **`business.conf`**：  
  定義業務邏輯相關的參數，可能包括 API 的業務邏輯配置。

- **`communication.conf`**：  
  配置 API 通訊相關的設定，如連線超時、重試機制等。

- **`core.conf`**：  
  核心服務的基本配置，確保專案的主要功能能正常運行。

- **`exit_code.txt`**：  
  記錄系統退出時的狀態碼，用於了解系統正常或異常結束的原因。

- **`log4j.properties`**：  
  日誌配置檔，用來指定日誌的輸出位置及格式，方便日後除錯和監控。

---

### **2.3 APIFV2.err**
`APIFV2.err` 是專案的錯誤報告檔案，記錄了專案執行期間的錯誤資訊。

#### **範例錯誤訊息：**
```
Still running according to PID file /PROG/ValerieTestAPIV2/APIFV2.pid, PID is 3546809
Service exit with a return value of 122
```

- **解析：**  
  這段錯誤訊息表示系統檢測到某個進程仍在運行（PID：3546809），且服務異常退出，返回值為 `122`。若遇到此錯誤，可以使用以下指令強制停止該進程：

  ```bash
  kill -9 3546809
  ```

---

### **2.4 lib**
`lib` 資料夾內包含所有專案的依賴項目，並已正確配置在 `CLASSPATH` 中。請使用此版本的 `lib` 目錄作為依賴庫的唯一來源。

---

### **2.5 APIFV2.jar**
`APIFV2.jar` 是專案的主要執行檔，負責啟動核心功能。

> **重要提醒：**  
  在執行 `APIFV2.jar` 時，**依賴外部配置檔案**，也就是 `APIFConfig` 目錄中的檔案。這些檔案的內容必須與當前測試環境中的設定保持一致，否則會出現錯誤。

#### **錯誤範例：**
```
Service exit with a return value of 122
```
這類錯誤常見於路徑或配置錯誤時。請務必檢查所有配置檔案，確保其內容符合測試環境的需求。

---

## **3. 結語**
完成上述配置與檢查後，即可順利啟動和運行 ValerieTestAPIV2 專案。若遇到任何問題，請檢查 `APIFV2.err` 中的錯誤訊息，並確保所有依賴庫和配置檔案都已正確設置。

---