import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080",
});

// 고정 확장자
export const getFixedExtensions = () => api.get("/extensions/fixed");
export const updateFixedExtension = (seq, isBlocked) =>
  api.put(`/extensions/fixed/${seq}?isBlocked=${isBlocked}`);

// 커스텀 확장자
export const getCustomExtensions = () => api.get("/extensions/custom");
export const addCustomExtension = (extension) =>
  api.post(`/extensions/custom?extension=${extension}`);
export const removeCustomExtension = (seq) =>
  api.delete(`/extensions/custom/${seq}`);

// 파일 업로드
export const uploadFile = (file) => {
  const formData = new FormData();
  formData.append("file", file);
  return api.post("/files/upload", formData, {
    headers: { "Content-Type": "multipart/form-data" },
  });
};
