package top.qx.ai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import top.qx.ai.entity.ChatSession;
import top.qx.ai.service.ChatSessionService;
import top.qx.starter.common.result.Result;

import java.util.List;

@RestController
@RequestMapping("/api/chat/sessions")
@RequiredArgsConstructor
@Tag(name = "会话管理接口")
public class ChatSessionController {

    private final ChatSessionService chatSessionService;

    @GetMapping
    @Operation(summary = "获取用户会话列表")
    public Result<List<ChatSession>> getUserSessions() {
        // 这里需要获取当前登录用户ID，暂时先用固定值1
        Long userId = 1L;
        List<ChatSession> sessions = chatSessionService.getUserSessions(userId);
        return Result.success(sessions);
    }

    @PostMapping
    @Operation(summary = "创建新会话")
    public Result<ChatSession> createSession(@RequestParam String title) {
        Long userId = 1L;
        ChatSession session = chatSessionService.createSession(userId, title);
        return Result.success(session);
    }

    @DeleteMapping("/{sessionId}")
    @Operation(summary = "删除会话")
    public Result<Boolean> deleteSession(@PathVariable Long sessionId) {
        Long userId = 1L;
        boolean success = chatSessionService.deleteSession(userId, sessionId);
        return Result.success(success);
    }

    @PutMapping("/{sessionId}/title")
    @Operation(summary = "更新会话标题")
    public Result<Boolean> updateSessionTitle(@PathVariable Long sessionId,
                                              @RequestParam String title) {
        Long userId = 1L;
        boolean success = chatSessionService.updateSessionTitle(userId, sessionId, title);
        return Result.success(success);
    }
}