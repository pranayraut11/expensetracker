import axios from 'axios';

const API_URL = '/analytics/average-category';

export const getAverageCategoryData = async (year = null, months = 12) => {
  const params = {};
  if (year) params.year = year;
  if (months) params.months = months;

  const response = await axios.get(API_URL, { params });
  return response.data;
};

