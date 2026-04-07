export const useInterview = () => {
  const config = useRuntimeConfig();
  const apiFetch = useApiFetch();

  const startInterview = async (domain: string) => {
    return await apiFetch<StartResponse>("/api/interview/start", {
      method: "POST",
      body: { domain },
    });
  };

  const submitAnswer = async (sessionId: string, answer: string) => {
    return await apiFetch<AnswerResponse>("/api/interview/answer", {
      method: "POST",
      body: { sessionId, answer },
    });
  };

  const getDomains = async () => {
    return await $fetch<string[]>("/api/domains", {
      baseURL: config.public.apiBase,
    });
  };

  const getHistory = async () => {
    return await apiFetch<InterviewHistoryItemResponse[]>(
      "/api/interview/history",
      {
        method: "GET",
      },
    );
  };

  const getHistorySession = async (sessionId: string) => {
    return await apiFetch<InterviewHistoryDetailResponse>(
      `/api/interview/history/${sessionId}`,
      {
        method: "GET",
      },
    );
  };

  const getAnalytics = async () => {
    return await apiFetch<InterviewAnalyticsResponse>(
      "/api/interview/analytics",
      {
        method: "GET",
      },
    );
  };

  return {
    startInterview,
    submitAnswer,
    getDomains,
    getHistory,
    getHistorySession,
    getAnalytics,
  };
};
export interface Feedback {
  score: number;
  strengths: string[];
  improvements: string[];
  summary: string;
}

export interface QuestionTurn {
  turnNumber: number;
  question: string;
  answer: string;
  feedback: Feedback;
}

export interface StartResponse {
  sessionId: string;
  question: string;
  topic?: string;
}

export interface AnswerResponse {
  history: QuestionTurn[];
  feedback: Feedback;
  nextQuestion?: string;
  topic?: string;
  finished: boolean;
}
export interface InterviewHistoryItemResponse {
  sessionId: string;
  domain: string;
  state: string;
  currentQuestion: string;
  historyCount: number;
}

export interface InterviewHistoryDetailResponse {
  sessionId: string;
  domain: string;
  ownerUserId: string;
  state: string;
  currentQuestion: string;
  history: QuestionTurn[];
}

export interface InterviewAnalyticsResponse {
  totalSessions: number;
  activeSessions: number;
  finishedSessions: number;
}
