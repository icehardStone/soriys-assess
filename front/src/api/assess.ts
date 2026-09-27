import request from '@/utils/request'
// 风险项
export interface Risks {
  falls: number          // 跌倒
  wandering: number      // 走失
  choking: number        // 噎食
  'suicide/self-harm': number  // 自杀、自伤
  other: number          // 其他
}

// 新建/更新评估的请求体
export interface AssessmentRequest {
  id?: number
  no?: string                    // 编号
  assessmentDate?: string        // 评估日期（YYYY-MM-DD）
  reason?: string                // 评估原因
  basic?: { name?: string }
  risks?: Risks                  // 风险
  provider?: string              // 提供者
  disease?: string[]             // 疾病
  diseaseOther?: string          // 其他疾病
  medications?: string[]         // 用药
  health?: string                // 健康状况
  answers?: Record<string, any>  // 答题
  totalScore?: number            // 总分
  initialLevel?: number          // 初评等级
  finalLevel?: number            // 最终等级
}

// 后端返回的评估记录
export interface AssessmentRecord extends AssessmentRequest {
  id: number
  createdBy?: number
  createdAt?: string
  updatedAt?: string
}

export interface AssessmentStatistics {
  total: number
  level0: number
  level1: number
  level2: number
  level3: number
  level4: number
}

// 分页返回
export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages?: number
}

// 分页查询参数
export interface AssessmentPageParams {
  current?: number
  size?: number
  keyword?: string
}
/**
 * 新建评估
 * POST /api/assessments
 */
export function createAssessment(data: AssessmentRequest): Promise<AssessmentRecord> {
  return request.post('/assessments', data)
}

/**
 * 更新评估
 * PUT /api/assessments/{id}
 */
export function updateAssessment(
  id: number,
  data: AssessmentRequest
): Promise<AssessmentRecord> {
  return request.put(`/assessments/${id}`, data)
}

/**
 * 获取单条评估
 * GET /api/assessments/{id}
 */
export function getAssessment(id: number): Promise<AssessmentRecord> {
  return request.get(`/assessments/${id}`)
}

/**
 * 分页查询评估列表
 * GET /api/assessments?current=1&size=10&keyword=xxx
 */
export function pageAssessments(
  params: AssessmentPageParams = {}
): Promise<PageResult<AssessmentRecord>> {
  const { current = 1, size = 10, keyword } = params
  return request.get('/assessments', {
    params: { current, size, keyword },
  })
}

/** 获取首页统计数据（不受最近记录分页影响）。 */
export function getAssessmentStatistics(): Promise<AssessmentStatistics> {
  return request.get('/assessments/statistics')
}

/**
 * 删除评估
 * DELETE /api/assessments/{id}
 */
export function deleteAssessment(id: number): Promise<{ message: string }> {
  return request.delete(`/assessments/${id}`)
}
