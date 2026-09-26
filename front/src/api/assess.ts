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
  no?: string                    // 编号
  assessmentDate?: string        // 评估日期（YYYY-MM-DD）
  reason?: string                // 评估原因
  elderlyName?: string           // 老人姓名
  gender?: string                // 性别
  birthDate?: string             // 出生日期
  height?: number                // 身高 cm
  weight?: number                // 体重 kg
  ethnicity?: string             // 民族
  religion?: string              // 宗教
  idNo?: string                  // 身份证号
  education?: string             // 文化程度
  living?: string                // 居住情况
  marriage?: string              // 婚姻状况
  risks?: Risks                  // 风险
  provider?: string              // 提供者
  disease?: string[]             // 疾病
  diseaseOther?: string          // 其他疾病
  medications?: string[]         // 用药
  health?: string                // 健康状况
  answers?: Record<string, any>  // 答题
  totalScore?: number            // 总分
  initialLevel?: string          // 初评等级
  finalLevel?: string            // 最终等级
}

// 后端返回的评估记录
export interface AssessmentRecord extends AssessmentRequest {
  id: number
  createdBy?: number
  createdAt?: string
  updatedAt?: string
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

/**
 * 删除评估
 * DELETE /api/assessments/{id}
 */
export function deleteAssessment(id: number): Promise<{ message: string }> {
  return request.delete(`/assessments/${id}`)
}