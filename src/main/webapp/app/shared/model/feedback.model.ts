export interface IFeedback {
  id?: number;
  caption?: string;
  email?: string;
  text?: any;
  feedbackResponse?: any;
  authorCustomerSummary?: string;
  authorId?: number;
}

export const defaultValue: Readonly<IFeedback> = {};
