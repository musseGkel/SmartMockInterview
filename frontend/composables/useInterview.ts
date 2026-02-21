export const useInterview = () => {
  const config = useRuntimeConfig();

  const startInterview = async (domain: string) => {
    return await $fetch("/api/interview/start", {
      baseURL: config.public.apiBase,
      method: "POST",
      body: { domain },
    });
  };

  const submitAnswer = async (sessionId: string, answer: string) => {
    return await $fetch("/api/interview/answer", {
      baseURL: config.public.apiBase,
      method: "POST",
      body: { sessionId, answer },
    });
  };

  const getDomains = async () => {
    return await $fetch("/api/domains", {
      baseURL: config.public.apiBase,
    });
  };

  return { startInterview, submitAnswer, getDomains };
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
  previousQuestion: string;
  previousTopic?: string;
  previousAnswer: string;
  feedback: Feedback;
  nextQuestion?: string;
  topic?: string;
  finished: boolean;
}
