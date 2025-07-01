package com.employee.filter;

import com.employee.entity.Employee;
import com.employee.service.EmployeeService;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
// @WebFilter(urlPatterns = {"/admin/*"})
@Order(2)
public class AdminAuthorizationFilter implements Filter {

    @Autowired
    private EmployeeService employeeService;

    // 權限映射：URL路徑 -> 所需權限
    private static final Map<String, String> URL_PERMISSION_MAP = new HashMap<>();
    
    static {
        // 會員管理權限
        URL_PERMISSION_MAP.put("/admin/member", "MEMBER_MANAGEMENT");
        URL_PERMISSION_MAP.put("/admin/listAllMember", "MEMBER_MANAGEMENT");
        URL_PERMISSION_MAP.put("/admin/addMember", "MEMBER_MANAGEMENT");
        
        // 員工管理權限
        URL_PERMISSION_MAP.put("/admin/staff", "STAFF_MANAGEMENT");
        URL_PERMISSION_MAP.put("/admin/staff1", "STAFF_MANAGEMENT");
        URL_PERMISSION_MAP.put("/admin/staff2", "STAFF_MANAGEMENT");
        URL_PERMISSION_MAP.put("/admin/staff3", "STAFF_MANAGEMENT");
        
        // 住宿管理權限
        URL_PERMISSION_MAP.put("/admin/room", "ROOM_MANAGEMENT");
        URL_PERMISSION_MAP.put("/admin/listAllRoomType", "ROOM_MANAGEMENT");
        URL_PERMISSION_MAP.put("/admin/listAllRoomTypeSchedule", "ROOM_MANAGEMENT");
        URL_PERMISSION_MAP.put("/admin/listAllRoom", "ROOM_MANAGEMENT");
        URL_PERMISSION_MAP.put("/admin/roomo_info", "ROOM_MANAGEMENT");
        
        // 餐廳管理權限
        URL_PERMISSION_MAP.put("/admin/resto", "RESTAURANT_MANAGEMENT");
        URL_PERMISSION_MAP.put("/admin/resto_info", "RESTAURANT_MANAGEMENT");
        URL_PERMISSION_MAP.put("/admin/resto_timeslot", "RESTAURANT_MANAGEMENT");
        URL_PERMISSION_MAP.put("/admin/resto_order", "RESTAURANT_MANAGEMENT");
        
        // 商店管理權限
        URL_PERMISSION_MAP.put("/admin/shop", "SHOP_MANAGEMENT");
        URL_PERMISSION_MAP.put("/admin/prod", "SHOP_MANAGEMENT");
        URL_PERMISSION_MAP.put("/admin/prodCart", "SHOP_MANAGEMENT");
        URL_PERMISSION_MAP.put("/admin/shopOrd", "SHOP_MANAGEMENT");
        
        // 優惠券管理權限
        URL_PERMISSION_MAP.put("/admin/coupon", "COUPON_MANAGEMENT");
        
        // 新聞管理權限
        URL_PERMISSION_MAP.put("/admin/news", "NEWS_MANAGEMENT");
        URL_PERMISSION_MAP.put("/admin/hotNews", "NEWS_MANAGEMENT");
        URL_PERMISSION_MAP.put("/admin/promotionNews", "NEWS_MANAGEMENT");
    }

    // 不需要權限驗證的路徑
    private static final String[] EXCLUDED_PATHS = {
        "/admin/login",
        "/admin/logout",
        "/admin/check-login",
        "/admin/static",
        "/admin/css",
        "/admin/js",
        "/admin/images",
        "/admin/vendor"
    };

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        // 暫時註解掉權限驗證，方便開發測試
        chain.doFilter(request, response);
        return;
        
        /*
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        
        String requestURI = httpRequest.getRequestURI();
        
        // 檢查是否為排除的路徑
        if (isExcludedPath(requestURI)) {
            chain.doFilter(request, response);
            return;
        }
        
        // 檢查是否需要權限驗證
        String requiredPermission = getRequiredPermission(requestURI);
        if (requiredPermission == null) {
            // 不需要特定權限，允許訪問
            chain.doFilter(request, response);
            return;
        }
        
        // 檢查權限
        if (session != null && session.getAttribute("adminEmployee") != null) {
            Employee employee = (Employee) session.getAttribute("adminEmployee");
            
            // 檢查是否有所需權限
            if (employeeService.hasPermission(employee.getEmployeeId(), requiredPermission)) {
                chain.doFilter(request, response);
                return;
            } else {
                // 沒有權限，返回403錯誤
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "您沒有權限訪問此功能");
                return;
            }
        }
        
        // 未登入，重定向到登入頁面
        httpResponse.sendRedirect("/admin/login");
        */
    }

    private String getRequiredPermission(String requestURI) {
        String path = requestURI;
        if (path.startsWith("/admin")) {
            // 只在長度大於6時才做substring，否則給空字串
            path = path.length() > 6 ? path.substring(6) : "";
        }
        // 查找最匹配的權限
        for (Map.Entry<String, String> entry : URL_PERMISSION_MAP.entrySet()) {
            if (path.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    private boolean isExcludedPath(String requestURI) {
        for (String excludedPath : EXCLUDED_PATHS) {
            if (requestURI.startsWith(excludedPath)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 初始化
    }

    @Override
    public void destroy() {
        // 清理資源
    }
} 